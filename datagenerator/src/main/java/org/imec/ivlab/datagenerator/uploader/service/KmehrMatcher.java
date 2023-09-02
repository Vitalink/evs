package org.imec.ivlab.datagenerator.uploader.service;

import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import java.util.stream.Collectors;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.kmehr.model.util.IDKmehrUtil;
import org.imec.ivlab.core.model.evsref.EVSREF;
import org.imec.ivlab.core.model.evsref.Identifiable;
import org.imec.ivlab.core.model.evsref.ListOfIdentifiables;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.model.upload.msentrylist.exception.IdenticalEVSRefsFoundException;
import org.imec.ivlab.core.model.upload.msentrylist.exception.NoMatchingEvsRefException;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.core.util.XmlFormatterUtil;
import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class KmehrMatcher {

    private final static Logger LOG = LogManager.getLogger(KmehrMatcher.class);

    public static <T extends ListOfIdentifiables> T addToList(T addList, T vaultList) throws IdenticalEVSRefsFoundException {

        T newList = SerializationUtils.clone(vaultList);

        if (addList == null || CollectionsUtil.emptyOrNull(addList.getIdentifiables())) {
            return newList;
        }

        for (Identifiable identifiable : addList.getIdentifiables()) {
            LOG.info("Entry " + identifiable.getReference().getFormatted() + " will be ADDED");
            Identifiable clone = SerializationUtils.clone(identifiable);
            newList.getIdentifiables().add(clone);
        }

        return newList;

    }

    public static <T extends ListOfIdentifiables> T removeFromList(T removeList, T vaultList) throws NoMatchingEvsRefException {

        if (removeList == null || CollectionsUtil.emptyOrNull(removeList.getIdentifiables())) {
            return vaultList;
        }

        T newList = SerializationUtils.clone(vaultList);

        for (Identifiable identifiable : removeList.getIdentifiables()) {
            if (!removeFromListByEvsRef(newList, identifiable.getReference())) {
                throw new NoMatchingEvsRefException("No EVSRef with reference " + identifiable.getReference().getFormatted()+ " found in vault");
            }
        }

        return newList;

    }

    /**
     * Attempts to find every EVSREF which exists the list of @listWithRefsToFind in the @vaultList
     * @param listWithRefsToFind
     * @param vaultList
     * @throws NoMatchingEvsRefException
     */
    public static <T extends ListOfIdentifiables> T ensureEVSRefsMatchWithVault(T listWithRefsToFind, T vaultList) throws NoMatchingEvsRefException {

        if (listWithRefsToFind == null || CollectionsUtil.emptyOrNull(listWithRefsToFind.getIdentifiables())) {
            return listWithRefsToFind;
        }

        T newList = SerializationUtils.clone(listWithRefsToFind);
        newList.getIdentifiables().clear();

        for (Identifiable identifiable : listWithRefsToFind.getIdentifiables()) {
            if (identifiable.getReference() != null) {
                Identifiable correspondingIdentifiable = findIdentifiableByReference(vaultList, identifiable.getReference());
                if (correspondingIdentifiable == null) {
                    throw new NoMatchingEvsRefException("No EVSRef with reference " + identifiable.getReference().getFormatted()+ " found in vault");
                }
                newList.getIdentifiables().add(correspondingIdentifiable);
            }
        }

        return newList;

    }

    public static <T extends ListOfIdentifiables> T mergeForUpdate(T listWithRefsToFind, T vaultList) throws NoMatchingEvsRefException {

        if (listWithRefsToFind == null || CollectionsUtil.emptyOrNull(listWithRefsToFind.getIdentifiables())) {
            return listWithRefsToFind;
        }

        T newList = SerializationUtils.clone(listWithRefsToFind);
        newList.getIdentifiables().clear();

        for (Identifiable identifiable : listWithRefsToFind.getIdentifiables()) {
            if (identifiable.getReference() != null) {
                Identifiable correspondingIdentifiable = findIdentifiableByReference(vaultList, identifiable.getReference());
                if (correspondingIdentifiable == null) {
                    throw new NoMatchingEvsRefException("No EVSRef with reference " + identifiable.getReference().getFormatted()+ " found in vault");
                }

                Identifiable combinedIdentifiable = combineContentAndLocalIds(identifiable, correspondingIdentifiable);

                newList.getIdentifiables().add(combinedIdentifiable);
            }
        }

        return newList;

    }


    private static boolean removeFromListByEvsRef(ListOfIdentifiables listOfIdentifiables, EVSREF evsref) {

        boolean removed = false;

        Iterator<Identifiable> iterator = listOfIdentifiables.getIdentifiables().iterator();
        while (iterator.hasNext()) {
            Identifiable identifiable = iterator.next();

            if (identifiable == null || identifiable.getReference() == null) {
                continue;
            }

            if (Objects.equals(identifiable.getReference(), evsref)) {
                LOG.info("Entry " + identifiable.getReference().getFormatted() + " will be REMOVED");
                iterator.remove();
                removed = true;
            }

        }

        return removed;

    }

    public static MSEntryList createMSEntryListForSpecificUpdates(MSEntryList uploadList, MSEntryList vaultList) {

        if (uploadList == null || CollectionsUtil.emptyOrNull(uploadList.getMsEntries())) {
            return new MSEntryList();
        }

        MSEntryList newList = SerializationUtils.clone(vaultList);

        for (MSEntry msEntryInVault : newList.getMsEntries().stream().filter(Objects::nonNull).collect(Collectors.toList())) {
            MSEntry msEntryLocalVersion = getEntryByReference(uploadList, msEntryInVault.getReference());
            // in vault and in upload list
            if (msEntryLocalVersion != null) {
                // in vault and in upload list, check if the entry was modified locally
                if (equal(msEntryLocalVersion, msEntryInVault)) {
                    LOG.info("MS entry " + msEntryInVault.getReference().getFormatted() + " will not be updated");
                } else {
                    LOG.info("MS entry " + msEntryInVault.getReference().getFormatted() + " will be UPDATED");

                    applyVaultEntryPropertiesToLocalEntry(msEntryInVault, msEntryLocalVersion);
                }
            } else {
                    LOG.info("MS entry " + msEntryInVault.getReference().getFormatted() + " will not be updated");
            }
        }

        return newList;

    }

    private static void applyVaultEntryPropertiesToLocalEntry(MSEntry msEntryInVault, MSEntry msEntryLocalVersion) {
        List<IDKMEHR> localIdMseTransactionVault = IDKmehrUtil.getIDKmehrs(msEntryInVault.getMseTransaction().getIds(), IDKMEHRschemes.LOCAL);
        MSEntry msEntryNewVersion = SerializationUtils.clone(msEntryLocalVersion);

        updateLocalIdInMSEntryList(msEntryNewVersion, localIdMseTransactionVault);
        updateDateTimeInMSEntry(msEntryNewVersion);

        msEntryInVault.setReference(msEntryNewVersion.getReference());
        msEntryInVault.setMseTransaction(msEntryNewVersion.getMseTransaction());
        msEntryInVault.setTsTransactions(msEntryNewVersion.getTsTransactions());
    }

    public static MSEntryList createMSEntryListForSchemeUpdate(MSEntryList uploadList, MSEntryList vaultList) {

        if (uploadList == null || CollectionsUtil.emptyOrNull(uploadList.getMsEntries())) {
            return new MSEntryList();
        }

        MSEntryList newList = SerializationUtils.clone(vaultList);

        Iterator<MSEntry> iterator = newList.getMsEntries().iterator();
        while (iterator.hasNext()) {
            MSEntry msEntryInVault = iterator.next();

            if (msEntryInVault == null) {
                continue;
            }

            // in vault without evs ref
            if (msEntryInVault.getReference() == null) {
                LOG.info("MS entry will be removed from vault because it doesn't have an EVSREF");
                iterator.remove();
                continue;
            }

            MSEntry msEntryLocalVersion = getEntryByReference(uploadList, msEntryInVault.getReference());

            // in vault but not in upload list
            if (msEntryLocalVersion == null) {
                LOG.info("MS entry " + msEntryInVault.getReference().getFormatted() + " will be REMOVED");
                iterator.remove();
                continue;
            }

            // in vault and in upload list, check if the entry was modified locally
            if (equal(msEntryLocalVersion, msEntryInVault)) {
                LOG.info("MS entry " + msEntryInVault.getReference().getFormatted() + " will not be updated");
            } else {
                LOG.info("MS entry " + msEntryInVault.getReference().getFormatted() + " will be UPDATED");

                applyVaultEntryPropertiesToLocalEntry(msEntryInVault, msEntryLocalVersion);

            }

        }


        for (MSEntry msEntryLocalVersion : uploadList.getMsEntries()) {

            MSEntry msEntryVaultVersion = getEntryByReference(vaultList, msEntryLocalVersion.getReference());

            if (msEntryVaultVersion == null) {

                if (msEntryLocalVersion.getReference() == null) {
                    LOG.info("an MS entry without EVSREF will be ADDED");
                } else {
                    LOG.info("MS entry " + msEntryLocalVersion.getReference().getFormatted() + " will be ADDED");
                }

                newList.getMsEntries().add(msEntryLocalVersion);
            }

        }

        return newList;

    }

    public static Identifiable combineContentAndLocalIds(Identifiable identifiableWithContentToKeep, Identifiable identifiableWithLocalIdsToKeep) {

        List<IDKMEHR> localIdsToKeep = IDKmehrUtil.getIDKmehrs(identifiableWithLocalIdsToKeep.getIdentifiableTransaction().getIds(), IDKMEHRschemes.LOCAL);
        Identifiable cloneContentToKeep = SerializationUtils.clone(identifiableWithContentToKeep);

        updateLocalIdInMSEntryList(cloneContentToKeep, localIdsToKeep);

        return cloneContentToKeep;

    }


    private static MSEntry getEntryByReference(MSEntryList msEntryList, EVSREF evsref) {
        if (msEntryList == null || CollectionsUtil.emptyOrNull(msEntryList.getMsEntries())) {
            return null;
        }

        for (MSEntry msEntry : msEntryList.getMsEntries()) {
            if (Objects.equals(msEntry.getReference(), evsref)) {
                return msEntry;
            }
        }

        return null;
    }

    private static Identifiable findIdentifiableByReference(ListOfIdentifiables listOfIdentifiables, EVSREF evsref) {

        if (listOfIdentifiables == null || CollectionsUtil.emptyOrNull(listOfIdentifiables.getIdentifiables())) {
            return null;
        }

        for (Identifiable identifiable : listOfIdentifiables.getIdentifiables()) {
            if (Objects.equals(identifiable.getReference(), evsref)) {
                return identifiable;
            }
        }

        return null;

    }

    /**
     * Compares the IdentifiableTransaction of an identifiable
     * @param identiableOne
     * @param identifiableTwo
     * @return
     */
    public static boolean equal(Identifiable identiableOne, Identifiable identifiableTwo) {

        if ((identiableOne == null && identifiableTwo != null) || (identifiableTwo == null && identiableOne != null)) {
            return false;
        }
        if (identiableOne == null) {
            return true;
        }
        if ((identiableOne.getIdentifiableTransaction() == null && identifiableTwo.getIdentifiableTransaction() != null) || (identifiableTwo.getIdentifiableTransaction() == null && identiableOne.getIdentifiableTransaction() != null)) {
            return false;
        }

        if (identiableOne.getIdentifiableTransaction() == null) {
            return true;
        }

        return IdentifiablesHaveSameContent(identiableOne, identifiableTwo);

    }

    private static boolean transactionsHaveSameContent(TransactionType transactionTypeOneOriginal, TransactionType transactionTypeTwoOriginal) {

        TransactionType transactionTypeOne = SerializationUtils.clone(transactionTypeOneOriginal);
        TransactionType transactionTypeTwo = SerializationUtils.clone(transactionTypeTwoOriginal);

        prepareTransactionForComparison(transactionTypeOne);
        prepareTransactionForComparison(transactionTypeTwo);

        try {

            String transactionTypeOneString = JAXBUtils.marshal(transactionTypeOne, "transaction");
            String transactionTypeTwoString = JAXBUtils.marshal(transactionTypeTwo, "transaction");

            String transactionTypeOneStringFormatted = transactionTypeOneString;
            String transactionTypeTwoStringFormatted = transactionTypeTwoString;

            try {

                String transactionTypeOneStringFormattedTemp = XmlFormatterUtil.format(transactionTypeOneString, false);
                String transactionTypeTwoStringFormattedTemp = XmlFormatterUtil.format(transactionTypeTwoString, false);

                transactionTypeOneStringFormatted = transactionTypeOneStringFormattedTemp;
                transactionTypeTwoStringFormatted = transactionTypeTwoStringFormattedTemp;

            } catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
                LOG.error("Failed to apply pretty print to xml before comparison. Will compare mse entries without pretty print", e);
            }

            return StringUtils.equals(transactionTypeOneStringFormatted, transactionTypeTwoStringFormatted);

        } catch (JAXBException e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }


    /**
     * For all transactions (any type) in the MSEntry, replace all LOCAL id with the supplied ones
     * Ideally local id's are not erased and taken from the original mse/ts transaction in the vault, for every transaction individually,
     * but that's not possible because in Vitalink ts transactions cannot be uniquely identified, so they now all get the local id from the MSE transaction
     * @param identifiable
     * @param idkmehrList
     */
    private static void updateLocalIdInMSEntryList(Identifiable identifiable, List<IDKMEHR> idkmehrList) {

        List<TransactionType> transactions = identifiable.getAllTransactions();

        if (CollectionsUtil.emptyOrNull(transactions)) {
            return;
        }

        for (TransactionType transactionType : transactions) {
            IDKmehrUtil.removeIDKmehrs(transactionType.getIds(), IDKMEHRschemes.LOCAL);
            IDKmehrUtil.addIDKmehrs(transactionType.getIds(), idkmehrList);
        }


    }

    public static void updateDateTimeInMSEntry(MSEntry msEntry) {

        List<TransactionType> transactions = msEntry.getAllTransactions();

        if (CollectionsUtil.emptyOrNull(transactions)) {
            return;
        }

        /*
        XMLGregorianCalendar calendarNow;
        try {
            calendarNow = DateUtils.toXmlGregorianCalendar(LocalDateTime.now());
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }*/
        DateTime now = DateTime.now();

        for (TransactionType transactionType : transactions) {
            transactionType.setDate(now);
            transactionType.setTime(now);
        }

    }
    
    private static boolean IdentifiablesHaveSameContent(Identifiable msEntryOne, Identifiable msEntryTwo) {

        List<TransactionType> msEntryOneAllTransactions = msEntryOne.getAllTransactions();
        List<TransactionType> msEntryTwoAllTransactions = msEntryTwo.getAllTransactions();

        if (CollectionsUtil.size(msEntryOneAllTransactions) != CollectionsUtil.size(msEntryTwoAllTransactions)) {
            return false;
        }

        for (int i = 0; i < CollectionsUtil.size(msEntryOneAllTransactions); i++) {
            if (!transactionsHaveSameContent(msEntryOneAllTransactions.get(i), msEntryTwoAllTransactions.get(i))) {
                return false;
            }
        }

        return true;

    }

    private static void prepareTransactionForComparison(TransactionType transactionType) {
        IDKmehrUtil.removeIDKmehrs(transactionType.getIds(), IDKMEHRschemes.ID_KMEHR);
        IDKmehrUtil.removeIDKmehrs(transactionType.getIds(), IDKMEHRschemes.LOCAL);
        transactionType.setDate(null);
        transactionType.setTime(null);
        transactionType.setAuthor(null);
    }
}
