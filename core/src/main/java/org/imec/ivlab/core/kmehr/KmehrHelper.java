package org.imec.ivlab.core.kmehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLNKvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.model.evsref.Identifiable;
import org.imec.ivlab.core.model.evsref.ListOfIdentifiables;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.IDKmehrUtil;
import org.imec.ivlab.core.kmehr.model.util.ItemUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;


public class KmehrHelper {

    private final static Logger LOG = Logger.getLogger(KmehrHelper.class);

    public KmehrHelper() {

    }

    public static void removeLocalIds(ListOfIdentifiables listOfIdentifiables) {

        if (listOfIdentifiables == null || CollectionsUtil.emptyOrNull(listOfIdentifiables.getIdentifiables())) {
            return;
        }

        for (Identifiable identifiable : listOfIdentifiables.getIdentifiables()) {
            removeLocalIds(identifiable);
        }

    }

    public static void removeLocalIds(MSEntryList msEntryList) {

        if (msEntryList == null || CollectionsUtil.emptyOrNull(msEntryList.getMsEntries())) {
            return;
        }

        for (MSEntry msEntry : msEntryList.getMsEntries()) {
            removeLocalIds(msEntry);
        }

    }

    public static void removeLocalIds(Identifiable identifiable) {

        if (identifiable == null || identifiable.getIdentifiableTransaction() == null) {
            return;
        }

        IDKmehrUtil.removeIDKmehrs(identifiable.getIdentifiableTransaction().getIds(), IDKMEHRschemes.LOCAL);

    }

    public static void removeLocalIds(MSEntry msEntry) {

        if (msEntry == null) {
            return;
        }

        if (CollectionsUtil.emptyOrNull(msEntry.getAllTransactions())) {
            return;
        }

        for (TransactionType transaction : msEntry.getAllTransactions()) {
            IDKmehrUtil.removeIDKmehrs(transaction.getIds(), IDKMEHRschemes.LOCAL);
        }

    }

    public static void replaceLocalIds(Kmehrmessage kmehrmessage, String localIdValue) {

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);
        List<TransactionType> transactions = FolderUtil.getTransactions(folderType, CDTRANSACTIONvalues.MEDICATIONSCHEMEELEMENT);
        transactions.addAll(FolderUtil.getTransactions(folderType, CDTRANSACTIONvalues.TREATMENTSUSPENSION));

        if (CollectionsUtil.emptyOrNull(transactions)) {
            return;
        }

        for (TransactionType transaction : transactions) {
            replaceLocalIds(transaction, localIdValue);
        }

    }


    public static void mergeKmehrs(Kmehrmessage newKmehrmessage, MSEntryList msEntryList) {

        if (msEntryList == null || CollectionsUtil.emptyOrNull(msEntryList.getMsEntries())) {
            return;
        }

        FolderType folderType = KmehrMessageUtil.getFolderType(newKmehrmessage);

        int transactionId = getHighestTransactionKmehrId(folderType.getTransactions());
        transactionId++;

        for (MSEntry msEntry : msEntryList.getMsEntries()) {

            List<TransactionType> transactionTypes = prepareTransactions(msEntry, transactionId);
            folderType.getTransactions().addAll(transactionTypes);

            transactionId += transactionTypes.size();

        }

    }

    public static HashMap<TransactionType, List<TransactionType>> groupMedicationAndSuspensions(List<TransactionType> medicationTransactions, List<TransactionType> suspensionTransactions) {

        HashMap<TransactionType, List<TransactionType>> medicationAndLinkedSuspensions = new LinkedHashMap<>();

        // no medication
        if (CollectionsUtil.emptyOrNull(medicationTransactions)) {

            // no medication and suspensions
            if (CollectionsUtil.notEmptyOrNull(suspensionTransactions)) {
                throw new RuntimeException("Cannot have suspensions without medication");
            }

            return medicationAndLinkedSuspensions;
        }

        // no suspensions
        if (CollectionsUtil.emptyOrNull(suspensionTransactions)) {
            for (TransactionType medicationTransaction : medicationTransactions) {
                medicationAndLinkedSuspensions.put(medicationTransaction, new ArrayList<>());
            }
            return medicationAndLinkedSuspensions;
        }

        // 1 medication item --> no link between suspensions in the incoming transaction needed, let's just link the suspension(s) to the medication transaction
        if (medicationTransactions.size() == 1) {
            medicationAndLinkedSuspensions.put(medicationTransactions.get(0), suspensionTransactions);
            return medicationAndLinkedSuspensions;
        }

        // more than 1 medication item. we expect every suspension transaction to be linked to a medication item through the 'isplannedfor' link
        ArrayList<TransactionType> medicationTransactionsClone = new ArrayList<>(medicationTransactions);
        ArrayList<TransactionType> suspensionTransactionsClone = new ArrayList<>(suspensionTransactions);

        ListIterator<TransactionType> medicationIterator = medicationTransactionsClone.listIterator();

        int linkedSuspensionsCount = 0;

        while (medicationIterator.hasNext()) {

            TransactionType medicationTransaction = medicationIterator.next();
            String medicationKmehrId = getKmehrId(medicationTransaction);
            List<TransactionType> linkedSuspensions = findTransactionsWithLinkToMedicationId(suspensionTransactionsClone, medicationKmehrId);
            linkedSuspensionsCount += CollectionsUtil.size(linkedSuspensions);

            medicationAndLinkedSuspensions.put(medicationTransaction, new ArrayList<>(linkedSuspensions));
            suspensionTransactionsClone.removeAll(linkedSuspensions);

        }

        if (linkedSuspensionsCount < suspensionTransactions.size()) {
            throw new RuntimeException("Some treatment suspension transactions don't link correctly to a medication scheme element transaction and can therefore not be linked. Please review the kmehr content");
        }

        return medicationAndLinkedSuspensions;

    }

    private static List<TransactionType> findTransactionsWithLinkToMedicationId(ArrayList<TransactionType> transactions, String medicationKmehrId) {

        List<TransactionType> matchingTransactions = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(transactions)) {
            return matchingTransactions;
        }

        ItemUtil itemUtil = new ItemUtil();
        TransactionUtil transactionUtil = new TransactionUtil();
        for (TransactionType transaction : transactions) {

            ItemType medicationItem = transactionUtil.getItem(transaction, CDITEMvalues.MEDICATION);
            List<LnkType> links = itemUtil.getLinks(medicationItem, CDLNKvalues.ISPLANNEDFOR);

            boolean linksToMedicationId = false;
            for (LnkType link : links) {
                if (StringUtils.contains(link.getURL(), "'" + medicationKmehrId + "'")) {
                    linksToMedicationId =  true;
                }
            }

            if (linksToMedicationId) {
                matchingTransactions.add(transaction);
            }

        }

        return matchingTransactions;

    }

    private static String getKmehrId(TransactionType transactionType) {

        IDKmehrUtil idKmehrUtil = new IDKmehrUtil();
        List<IDKMEHR> idKmehrs = idKmehrUtil.getIDKmehrs(transactionType.getIds(), IDKMEHRschemes.ID_KMEHR);

        if (CollectionsUtil.emptyOrNull(idKmehrs)) {
            throw new RuntimeException("No kmehr id found in transaction. This should never happen!");
        }

        if (idKmehrs.size() > 1) {
            LOG.warn("Found " + idKmehrs.size() + " kmehr ids in transaction. This is wrong data. We'll pick the first id");
        }

        return idKmehrs.get(0).getValue();

    }


    private static IDKMEHR createIDKmehr(String value) {
        IDKMEHR idKmehr = new IDKMEHR();
        idKmehr.setSV("1.0");
        idKmehr.setS(IDKMEHRschemes.ID_KMEHR);
        idKmehr.setValue(value);
        return idKmehr;
    }


    private static IDKMEHR createIDKmehrVitalinkURI(String value) {
        IDKMEHR idKmehr = new IDKMEHR();
        idKmehr.setSV("1.0");
        idKmehr.setS(IDKMEHRschemes.LOCAL);
        idKmehr.setSL("vitalinkuri");
        idKmehr.setValue(value);
        return idKmehr;
    }

    public static void replaceIds(TransactionType transactionType, String id) {
        IDKmehrUtil idKmehrUtil = new IDKmehrUtil();
        idKmehrUtil.removeIDKmehrs(transactionType.getIds(), IDKMEHRschemes.ID_KMEHR);
        transactionType.getIds().add(createIDKmehr(id));
    }

    public static void replaceLocalIds(TransactionType transactionType, String localIdValue) {
        IDKmehrUtil idKmehrUtil = new IDKmehrUtil();
        idKmehrUtil.removeIDKmehrs(transactionType.getIds(), IDKMEHRschemes.LOCAL);
        transactionType.getIds().add(createIDKmehrVitalinkURI(localIdValue));
    }

    private static List<TransactionType> assignIds(TransactionType medicationTransactionOriginal, List<TransactionType> suspensionTransactionsOriginal, int startingTransactionId) {

        List<TransactionType> outgoingTransactions = new ArrayList<>();

        int transactionId = startingTransactionId;
        int medicationTransactionId = startingTransactionId;

        TransactionUtil transactionUtil = new TransactionUtil();

        TransactionType medicationTransaction = SerializationUtils.clone(medicationTransactionOriginal);

        replaceIds(medicationTransaction, String.valueOf(transactionId));

        transactionId++;

        outgoingTransactions.add(medicationTransaction);

        if (CollectionsUtil.notEmptyOrNull(suspensionTransactionsOriginal)) {
            for (TransactionType suspensionTransactionOriginal : suspensionTransactionsOriginal) {
                TransactionType suspensionTransaction = SerializationUtils.clone(suspensionTransactionOriginal);

                replaceIds(suspensionTransaction, String.valueOf(transactionId));

                transactionId++;

                ItemType medicationItem = transactionUtil.getItem(suspensionTransaction, CDITEMvalues.MEDICATION);

                medicationItem.getLnks().clear();
                LnkType lnkType = new LnkType();
                lnkType.setTYPE(CDLNKvalues.ISPLANNEDFOR);
                lnkType.setURL("//transaction[id[@S='ID-KMEHR']='" + medicationTransactionId + "']");
                medicationItem.getLnks().add(lnkType);

                outgoingTransactions.add(suspensionTransaction);

            }
        }

        return outgoingTransactions;


    }

    private static List<TransactionType> prepareTransactions(final MSEntry msEntry, int firstTransactionId) {

        List<TransactionType> outgoingTransactions = new ArrayList<>();

        List<TransactionType> transactionsWithCorrectIds = assignIds(msEntry.getMseTransaction(), msEntry.getTsTransactions(), firstTransactionId);
        outgoingTransactions.addAll(transactionsWithCorrectIds);

        return outgoingTransactions;

    }

    private static int getHighestTransactionKmehrId(List<TransactionType> transactionTypes) {

        IDKmehrUtil idKmehrUtil = new IDKmehrUtil();

        if (transactionTypes == null) {
            return 0;
        }

        int highest = 0;

        for (TransactionType transactionType : transactionTypes) {
            List<IDKMEHR> idKmehrs = idKmehrUtil.getIDKmehrs(transactionType.getIds(), IDKMEHRschemes.ID_KMEHR);
            for (IDKMEHR idKmehr : idKmehrs) {
                try {
                    Integer idValue = Integer.valueOf(idKmehr.getValue());
                    if (idValue > highest) {
                        highest = idValue;
                    }
                } catch (NumberFormatException e) {
                    LOG.error("Failed to parse IDKmehr value to integer: " + idKmehr.getValue(), e);
                }
            }

        }

        return highest;

    }

}
