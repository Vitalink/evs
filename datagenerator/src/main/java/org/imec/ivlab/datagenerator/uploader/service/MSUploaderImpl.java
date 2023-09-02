package org.imec.ivlab.datagenerator.uploader.service;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;
import org.imec.ivlab.core.authentication.model.AuthenticationConfig;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.kmehr.KmehrHelper;
import org.imec.ivlab.core.kmehr.model.util.RegimenUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.evsref.extractor.impl.MSEVSRefExtractor;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.model.upload.msentrylist.ReferenceDateUtil;
import org.imec.ivlab.core.model.upload.msentrylist.exception.IdenticalEVSRefsFoundException;
import org.imec.ivlab.core.model.upload.msentrylist.exception.MissingEVSRefException;
import org.imec.ivlab.core.model.upload.msentrylist.exception.MultipleEVSRefsInTransactionFoundException;
import org.imec.ivlab.core.model.upload.msentrylist.exception.NoMatchingEvsRefException;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.datagenerator.uploader.dateshift.ShiftAction;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;
import org.imec.ivlab.ehconnector.business.HubHelper;
import org.imec.ivlab.ehconnector.business.medicationscheme.MSService;
import org.imec.ivlab.ehconnector.business.medicationscheme.MSServiceImpl;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Days;
import java.util.List;

public class MSUploaderImpl implements Uploader, MSUploader {

    private final static Logger log = LogManager.getLogger(MSUploaderImpl.class);

    private MSService msDao;
    private HubHelper helper = new HubHelper();
    private String startTransactionId = "1";
    private ShiftAction shiftAction;
    private MSEVSRefExtractor refExtractor;

    public MSUploaderImpl() throws VitalinkException {
        this.msDao = new MSServiceImpl();
        this.refExtractor = new MSEVSRefExtractor();
    }

    @Override
    public void add(Patient patient, MSEntryList msEntryListToAdd, String actorId) throws UploaderException, VitalinkException {

        try {
            refExtractor.extractEVSRefs(msEntryListToAdd);
        } catch (MultipleEVSRefsInTransactionFoundException e) {
            throw new UploaderException(e);
        }

        if (CollectionsUtil.emptyOrNull(msEntryListToAdd.getMsEntries())) {
            log.info("No medication entries provided; No request will be sent to Vitalink.");
            return;
        } else {
            log.info("About to add " + CollectionsUtil.size(msEntryListToAdd.getMsEntries()) + " medication entries to the vault for patientID: " + patient.getId());
        }

        msDao.authenticate(readAuthenticationConfig(actorId));

        try {

            Kmehrmessage kmehrmessageTemplate = getKmehrmessageTemplate(patient);
            // apply shift action to msEntries that will be added only
            applyShiftAction(shiftAction, kmehrmessageTemplate, msEntryListToAdd);

            MSEntryList msEntryListInVault = msDao.getMedicationScheme(patient);
            refExtractor.extractEVSRefs(msEntryListInVault);
            refExtractor.generateEVSRefsIfMissing(msEntryListToAdd, msEntryListInVault);

            KmehrHelper.removeLocalIds(msEntryListToAdd);

            MSEntryList newEntrylist = KmehrMatcher.addToList(msEntryListToAdd, msEntryListInVault);
            refExtractor.validateEVSRefUniqueness(newEntrylist);

            msDao.putMedicationScheme(patient, kmehrmessageTemplate, newEntrylist);

        } catch (IdenticalEVSRefsFoundException e) {
            throw new UploaderException("Provided EVSRef exists already in vault", e);
        } catch (Exception e) {
            throw new VitalinkException(e);
        }

    }

    @Override
    public void replace(Patient patient, MSEntryList msEntryList, String actorId) throws VitalinkException, UploaderException {

        try {
            refExtractor.extractEVSRefs(msEntryList);
            refExtractor.validateEVSRefUniqueness(msEntryList);
            refExtractor.generateEVSRefsIfMissing(msEntryList, null);
        } catch (IdenticalEVSRefsFoundException | MultipleEVSRefsInTransactionFoundException e) {
            throw new UploaderException(e);
        }

        log.info("About to replace the medication entries in the vault with " + CollectionsUtil.size(msEntryList.getMsEntries()) + " medication entries for patientID: " + patient.getId());

        msDao.authenticate(readAuthenticationConfig(actorId));

        KmehrHelper.removeLocalIds(msEntryList);

        Kmehrmessage kmehrmessageTemplate = getKmehrmessageTemplate(patient);
        // apply shift action to all msEntries that will replace the ones in the vault
        applyShiftAction(shiftAction, kmehrmessageTemplate, msEntryList);

        msDao.putMedicationScheme(patient, kmehrmessageTemplate, msEntryList);

    }

    @Override
    public void generateREF(Patient patient, MSEntryList msEntryList, String actorId) throws VitalinkException, UploaderException {

        msDao.authenticate(readAuthenticationConfig(actorId));

        MSEntryList msEntryListVaultBeforeGeneratingRefs = msDao.getMedicationScheme(patient);

        MSEntryList msEntryListVault = SerializationUtils.clone(msEntryListVaultBeforeGeneratingRefs);

        try {
            refExtractor.extractEVSRefs(msEntryListVault);
            refExtractor.validateEVSRefUniqueness(msEntryListVault);
            refExtractor.generateEVSRefsIfMissing(msEntryListVault, null);

            if (msEntryListVault != null) {
                for (int i = 0; i < msEntryListVaultBeforeGeneratingRefs.getMsEntries().size(); i++) {
                    if (! KmehrMatcher.equal(msEntryListVaultBeforeGeneratingRefs.getMsEntries().get(i), msEntryListVault.getMsEntries().get(i))) {
                        KmehrMatcher.updateDateTimeInMSEntry(msEntryListVault.getMsEntries().get(i));
                    }
                }
            }

        } catch (IdenticalEVSRefsFoundException | MultipleEVSRefsInTransactionFoundException e) {
            throw new UploaderException(e);
        }

        msDao.putMedicationScheme(patient, getKmehrmessageTemplate(patient), msEntryListVault);

    }

    @Override
    public void removeREF(Patient patient, MSEntryList msEntryListToRemove, String actorId) throws VitalinkException, UploaderException {

        if (CollectionsUtil.emptyOrNull(msEntryListToRemove.getMsEntries())) {
            throw new UploaderException("No medication entries were provided");
        }

        msDao.authenticate(readAuthenticationConfig(actorId));

        MSEntryList msEntryListVault;
        try {
            refExtractor.extractEVSRefs(msEntryListToRemove);
            refExtractor.validatePresenceEVSRefs(msEntryListToRemove);

            log.info("About to remove " + CollectionsUtil.size(msEntryListToRemove.getMsEntries()) + " medication entries from the vault for patientID: " + patient.getId());

            msEntryListVault = msDao.getMedicationScheme(patient);
            refExtractor.extractEVSRefs(msEntryListVault);

            MSEntryList msEntryListForRemoval = KmehrMatcher.removeFromList(msEntryListToRemove, msEntryListVault);

            msDao.putMedicationScheme(patient, getKmehrmessageTemplate(patient), msEntryListForRemoval);

        } catch (MultipleEVSRefsInTransactionFoundException | MissingEVSRefException | NoMatchingEvsRefException e) {
            throw new UploaderException(e);
        }

    }

    public void updateREF(Patient patient, MSEntryList msEntryList, String actorId) throws VitalinkException, UploaderException {

        msDao.authenticate(readAuthenticationConfig(actorId));

        try {
            KmehrHelper.removeLocalIds(msEntryList);
            refExtractor.extractEVSRefs(msEntryList);
            refExtractor.validateEVSRefUniqueness(msEntryList);
            refExtractor.validatePresenceEVSRefs(msEntryList);
        } catch (MultipleEVSRefsInTransactionFoundException | IdenticalEVSRefsFoundException | MissingEVSRefException e) {
            throw new UploaderException(e);
        }

        MSEntryList msEntryListVault = msDao.getMedicationScheme(patient);
        try {
            refExtractor.extractEVSRefs(msEntryListVault);
            KmehrMatcher.ensureEVSRefsMatchWithVault(msEntryList, msEntryListVault);
        } catch (MultipleEVSRefsInTransactionFoundException | NoMatchingEvsRefException e) {
            throw new UploaderException(e);
        }

        Kmehrmessage kmehrmessageTemplate = getKmehrmessageTemplate(patient);
        applyShiftAction(shiftAction, kmehrmessageTemplate, msEntryList);
        MSEntryList msEntrylistForPut = KmehrMatcher.createMSEntryListForSpecificUpdates(msEntryList, msEntryListVault);

        msDao.putMedicationScheme(patient, kmehrmessageTemplate, msEntrylistForPut);

    }

    @Override
    public void updateschemeREF(Patient patient, MSEntryList msEntryList, String actorId) throws VitalinkException, UploaderException {

        msDao.authenticate(readAuthenticationConfig(actorId));

        MSEntryList msEntryListVault;
        try {
            KmehrHelper.removeLocalIds(msEntryList);
            refExtractor.extractEVSRefs(msEntryList);
            refExtractor.generateEVSRefsIfMissing(msEntryList, null);
            refExtractor.validateEVSRefUniqueness(msEntryList);

            log.info("About to update the medication scheme by reference for patientID: " + patient.getId());

            msEntryListVault = msDao.getMedicationScheme(patient);
            refExtractor.extractEVSRefs(msEntryListVault);

        } catch (MultipleEVSRefsInTransactionFoundException | IdenticalEVSRefsFoundException e) {
            throw new UploaderException(e);
        }

        Kmehrmessage kmehrmessageTemplate = getKmehrmessageTemplate(patient);
        // apply shift action to all msEntries from the input, which will then be compared with the ones in the vault
        applyShiftAction(shiftAction, kmehrmessageTemplate, msEntryList);

        MSEntryList msEntrylistForPut = KmehrMatcher.createMSEntryListForSchemeUpdate(msEntryList, msEntryListVault);


        msDao.putMedicationScheme(patient, kmehrmessageTemplate, msEntrylistForPut);

    }

    @Override
    public void setStartTransactionId(String startTransactionId) {
        this.startTransactionId = startTransactionId;
    }


    @Override
    public void setShiftAction(ShiftAction shiftAction) {
        this.shiftAction = shiftAction;
    }

    private AuthenticationConfig readAuthenticationConfig(String actorId) {
        AuthenticationConfig config = AuthenticationConfigReader.loadByName(actorId);
        return config;
    }

    private Kmehrmessage getKmehrmessageTemplate(Patient patient) throws UploaderException {
        try {
            return helper.createTransactionSetMessage(patient, startTransactionId);
        } catch (Exception e) {
            throw new UploaderException(e);
        }
    }

    private void applyShiftAction(ShiftAction shiftAction, Kmehrmessage kmehrmessageTemplate, MSEntryList msEntryList) {

        if (ShiftAction.NO_TAG_AND_NO_SHIFT.equals(shiftAction)) { return; }

        LocalDate newReferenceDate = LocalDate.now();

        if (ShiftAction.TAG_AND_NO_SHIFT.equals(shiftAction)) {

            log.debug("Setting new reference date to today");

            // tag
            ReferenceDateUtil.setReferenceDate(kmehrmessageTemplate, newReferenceDate);

            for (MSEntry msEntry : msEntryList.getMsEntries()) {
                msEntry.setReferenceDate(newReferenceDate);
            }
        }

        if (ShiftAction.SHIFT_AND_TAG.equals(shiftAction)) {

            // shift
            for (MSEntry msEntry : msEntryList.getMsEntries()){
                shiftDates(msEntry, newReferenceDate);
            }

            log.debug("Setting new reference date to today");

            // tag
            ReferenceDateUtil.setReferenceDate(kmehrmessageTemplate, newReferenceDate);

            for (MSEntry msEntry : msEntryList.getMsEntries()) {
                msEntry.setReferenceDate(newReferenceDate);
            }

        }

    }

    /**
     * Shifts dates in all medication items of a msEntry. The shift value is the difference between the new reference date and the reference date from the msEntry.
     * After shifting, the reference date on the medication item is not changed.
     * The shift value is the difference between the new reference date and the reference date from the msEntry.
     * Only following fields are shifted: beginmoment date, endmoment date, regimen date
     * Example: if the new reference date = reference date from msEntry + 2, then dates are shifted 2 days ahead.
     * @param msEntry
     * @param newReferenceDate
     */
    private void shiftDates(MSEntry msEntry, LocalDate newReferenceDate) {

        LocalDate oldReferenceDate = msEntry.getReferenceDate();

        if (oldReferenceDate == null) {
            log.warn("No ReferenceDate provided for medication scheme element; cannot shift dates for this medication scheme element");
            return;
        }

        LocalDateTime oldReferenceDateTime = new LocalDateTime(oldReferenceDate.getYear(), oldReferenceDate.getMonthOfYear(), oldReferenceDate.getDayOfMonth(), 0, 0, 0);
        LocalDateTime newReferenceDateTime = new LocalDateTime(newReferenceDate.getYear(), newReferenceDate.getMonthOfYear(), newReferenceDate.getDayOfMonth(), 0, 0, 0);
        int daysDiff = Days.daysBetween(oldReferenceDateTime, newReferenceDateTime).getDays();
        if (daysDiff == 0) {
            log.debug("Reference date equals date of today, there's no need to shift dates");
            return;
        }
        if (daysDiff > 0) {
            log.debug("Reference date was " + daysDiff + " days old. Will shift dates forward by " + daysDiff + " days. Dates: beginmoment date, endmoment date, regimen date");
        } else {
            log.debug("Reference date was " + Math.abs(daysDiff) + " days ahead of today. Will shift dates back by " + Math.abs(daysDiff) + " days. Dates: beginmoment date, endmoment date, regimen date");
        }

        for (TransactionType transactionType : msEntry.getAllTransactions()) {

            List<ItemType> medicationItems = TransactionUtil.getItems(transactionType, CDITEMvalues.MEDICATION);

            if (CollectionsUtil.emptyOrNull(medicationItems)) {
                continue;
            }

            for (ItemType medicationItem : medicationItems) {

                if (medicationItem.getBeginmoment() != null) {
                    medicationItem.getBeginmoment().setDate(
                        medicationItem.getBeginmoment().getDate().plusDays(daysDiff)
                    );
                }
                if (medicationItem.getEndmoment() != null) {
                    medicationItem.getEndmoment().setDate(
                        medicationItem.getEndmoment().getDate().plusDays(daysDiff)
                    );
                }

                List<java.util.Calendar> dates = RegimenUtil.getDates(medicationItem.getRegimen());
                if (dates != null) {
                    for (java.util.Calendar date : dates) {
                        date.add(java.util.Calendar.DATE, daysDiff);
                    }
                }

            }


        }

    }

}
