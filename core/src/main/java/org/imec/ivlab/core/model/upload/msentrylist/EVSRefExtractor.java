package org.imec.ivlab.core.model.upload.msentrylist;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.constants.CoreConstants;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.evsref.EVSREF;
import org.imec.ivlab.core.model.upload.msentrylist.exception.IdenticalEVSRefsFoundException;
import org.imec.ivlab.core.model.upload.msentrylist.exception.MissingEVSRefException;
import org.imec.ivlab.core.model.upload.msentrylist.exception.MultipleEVSRefsInTransactionFoundException;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.core.util.RandomGenerator;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.imec.ivlab.core.kmehr.model.util.TransactionUtil.getTransactionId;
import static org.imec.ivlab.core.model.evsref.EVSREF.FIND_EVSREF_REGULAR_EXPRESSION;

public class EVSRefExtractor {

    private final static Logger LOG = Logger.getLogger(EVSRefExtractor.class);

    public static void validatePresenceEVSRefs(MSEntryList msEntryList) throws MissingEVSRefException {

        if (msEntryList == null || CollectionsUtil.emptyOrNull(msEntryList.getMsEntries())) {
            return;
        }

        for (MSEntry msEntry : msEntryList.getMsEntries()) {
            if (msEntry.getReference() == null) {
                String transactionId = TransactionUtil.getTransactionId(msEntry.getMseTransaction());
                String errorMessage = "EVSRef is missing";
                errorMessage = errorMessage + (StringUtils.isBlank(transactionId) ? "" : " for transaction with id: " + transactionId);
                throw new MissingEVSRefException(errorMessage);
            }
        }

    }

    public static void validateEVSRefUniqueness(MSEntryList msEntryList) throws IdenticalEVSRefsFoundException {

        if (msEntryList == null || CollectionsUtil.emptyOrNull(msEntryList.getMsEntries())) {
            return;
        }

        Set<EVSREF> usedRefs = new HashSet<>();

        for (MSEntry msEntry : msEntryList.getMsEntries()) {
            if (msEntry.getReference() != null) {

                if (usedRefs.contains(msEntry.getReference())) {
                    throw new IdenticalEVSRefsFoundException("Identical EVS reference found across different transactions: " + msEntry.getReference().getFormatted());
                }

                usedRefs.add(msEntry.getReference());
            }
        }

    }


    private static Set<EVSREF> getEVSRefsInMsEntryList(MSEntryList msEntryList) {

        Set<EVSREF> usedRefs = new HashSet<>();

        if (msEntryList == null || CollectionsUtil.emptyOrNull(msEntryList.getMsEntries())) {
            return usedRefs;
        }

        for (MSEntry msEntry : msEntryList.getMsEntries()) {
            if (msEntry.getReference() != null) {
                usedRefs.add(msEntry.getReference());
            }
        }

        return usedRefs;

    }

    public static void generateEVSRefsIfMissing(MSEntryList msEntryList, MSEntryList msEntryListWithTakenEvsRefs) {

        if (msEntryList == null || CollectionsUtil.emptyOrNull(msEntryList.getMsEntries())) {
            return;
        }

        Set<EVSREF> usedRefs = getEVSRefsInMsEntryList(msEntryList);
        usedRefs.addAll(getEVSRefsInMsEntryList(msEntryListWithTakenEvsRefs));

        for (MSEntry msEntry : msEntryList.getMsEntries()) {
            if (msEntry.getReference() == null) {
                EVSREF newReference = createUniqueEVSRef(usedRefs);
                LOG.info("Created EVS reference " + newReference + " for transaction with id: " + getTransactionId(msEntry.getMseTransaction()));
                putEvsReference(msEntry.getMseTransaction(), newReference);
                msEntry.setReference(newReference);
            }
        }


    }

    public static void extractEVSRefs(MSEntryList msEntryList) throws MultipleEVSRefsInTransactionFoundException {

        if (msEntryList == null || CollectionsUtil.emptyOrNull(msEntryList.getMsEntries())) {
            return;
        }

        for (MSEntry msEntry : msEntryList.getMsEntries()) {
            if (msEntry == null) {
                continue;
            }

            try {
                String transactionString = JAXBUtils.marshal(msEntry.getMseTransaction());
                List<EVSREF> evsRefs = findEVSREFs(transactionString);

                if (CollectionsUtil.size(evsRefs) > 1) {
                    throw new MultipleEVSRefsInTransactionFoundException("Multiple EVS references found within 1 MSE transaction: " + evsRefs);
                } else if (CollectionsUtil.size(evsRefs) == 1) {
                    msEntry.setReference(evsRefs.get(0));
                }

            } catch (JAXBException e) {
                LOG.error(e);
            }


        }

    }

    private static List<EVSREF> findEVSREFs(String kmehrContent) {

        List<EVSREF> evsRefs = new ArrayList<>();

        Pattern pattern = Pattern.compile(FIND_EVSREF_REGULAR_EXPRESSION, Pattern.DOTALL + Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);

        Matcher matcher = pattern.matcher(kmehrContent);

        while (matcher.find()) {
            evsRefs.add(new EVSREF(matcher.group(1)));
        }

        return evsRefs;

    }

    private static EVSREF createUniqueEVSRef(Set<EVSREF> exceptList) {

        EVSREF newRef = new EVSREF("g" + RandomGenerator.getBaseNumber(5));
        while (CollectionsUtil.notEmptyOrNull(exceptList) && exceptList.contains(newRef)) {
            newRef = new EVSREF("g" + RandomGenerator.getBaseNumber(5));
        }
        return newRef;

    }

    private static void putEvsReference(TransactionType transaction, EVSREF evsref) {

        ItemType medicationItem = TransactionUtil.getItem(transaction, CDITEMvalues.MEDICATION);

        TextType instructionforpatient = medicationItem.getInstructionforpatient();
        if (instructionforpatient == null) {
            instructionforpatient = new TextType();
            instructionforpatient.setL(CoreConstants.KMEHR_LANGUAGE_L_ATTRIBUTE);
            instructionforpatient.setValue("");
        }

        instructionforpatient.setValue(StringUtils.joinWith("", instructionforpatient.getValue(), evsref.getFormatted()));

        medicationItem.setInstructionforpatient(instructionforpatient);
    }


}
