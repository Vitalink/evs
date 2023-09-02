package org.imec.ivlab.ehconnector.business.medicationscheme;

import static org.imec.ivlab.ehconnector.hub.util.AuthorUtil.regenerateTransactionAuthorBasedOnCertificate;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionListResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionSetResponse;
import be.fgov.ehealth.hubservices.core.v3.TransactionSummaryType;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.config.EVSConfig;
import org.imec.ivlab.core.config.EVSProperties;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.kmehr.KmehrHelper;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.IDKmehrUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.model.upload.msentrylist.MedicationSchemeExtractor;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.ehconnector.business.AbstractService;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.InvalidNumberOfDataEntriesAvailableWithinRequestException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.RemoveNotAllowedIfDataEntryAlreadyDeleted;
import org.imec.ivlab.ehconnector.hub.exception.incurable.SubjectWithSSINUnknownException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.TransactionNotFoundException;
import org.imec.ivlab.ehconnector.hubflow.HubFlow;
import org.imec.ivlab.ehconnector.util.TransactionHelper;

public class MSServiceImpl extends AbstractService implements MSService {

    private final static Logger log = LogManager.getLogger(MSServiceImpl.class);

    private static final TransactionType TRANSACTION_TYPE = TransactionType.MEDICATION_SCHEME;
    private HubFlow hubFlow = new HubFlow();

    public MSServiceImpl() throws VitalinkException {
    }

    @Override
    public MSEntryList getMedicationScheme(Patient patient) throws VitalinkException {
        GetTransactionSetResponse getTransactionSetResponse = null;
        try {
            getTransactionSetResponse = hubFlow.getTransactionSet(patient.getId(), TRANSACTION_TYPE);
            Kmehrmessage kmehrMessageInVault = getTransactionSetResponse.getKmehrmessage();
            return MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrMessageInVault);
        } catch (TransactionNotFoundException e) {
            return new MSEntryList();
        } catch (SubjectWithSSINUnknownException e) {
            return new MSEntryList();
        } catch (Exception e) {
            throw new VitalinkException(e);
        }
    }

    @Override
    public void putMedicationScheme(Patient patient, Kmehrmessage kmehrmessageTemplate, MSEntryList msEntryList) throws VitalinkException {

        try {

            Kmehrmessage kmehrmessageForPut = fillSchemeVersion(patient, kmehrmessageTemplate);
            kmehrmessageForPut = fillSchemeIdentifier(patient, kmehrmessageForPut);
            KmehrHelper.mergeKmehrs(kmehrmessageForPut, msEntryList);
            kmehrmessageForPut.setSignature(null);

            regenerateMedicationSchemeAuthor(kmehrmessageForPut);

            hubFlow.putTransactionSet(patient.getId(), TRANSACTION_TYPE, kmehrmessageForPut);
        } catch (RemoveNotAllowedIfDataEntryAlreadyDeleted e) {
            log.info("Got a " + RemoveNotAllowedIfDataEntryAlreadyDeleted.class.getName() + " exception. Probably the vault was already empty and we tried to empty the vault again");
        } catch (InvalidNumberOfDataEntriesAvailableWithinRequestException e) {
            log.info("Got a " + InvalidNumberOfDataEntriesAvailableWithinRequestException.class.getName() + " exception. Probably the medication scheme in our putTransactionSetMessage is not any different from the medication scheme in the vault");
        } catch (Exception e) {
            throw new VitalinkException(e);
        }

    }

    private void regenerateMedicationSchemeAuthor(Kmehrmessage kmehrmessage) throws TechnicalConnectorException {

        boolean mustRegenerateKmehrMstransactionAuthor =
            Optional.ofNullable(EVSConfig.getInstance().getPropertyOrNull(EVSProperties.AUTO_GENERATE_KMEHR_MS_TRANSACTION_AUTHOR))
                .map(value -> StringUtils.equalsIgnoreCase(value, "true"))
                .orElse(false);

        if (!mustRegenerateKmehrMstransactionAuthor) {
            return;
        }

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);
        be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType transactionMS = FolderUtil.getTransaction(folderType, CDTRANSACTIONvalues.MEDICATIONSCHEME);

        regenerateTransactionAuthorBasedOnCertificate(transactionMS);

    }

    private Kmehrmessage fillSchemeVersion(Patient patient, Kmehrmessage kmehrmessageTemplate) throws Exception {

        String latestVersion = getLatestVersion(patient);

        // RSW requires version "0" for initial PutTransactionSetRequest
        // Vitalink works with "" in that case.
        // TODO: check if Vitalink also accepts "0" in that case.
        if (latestVersion == null) {
            latestVersion = "0";
        }

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessageTemplate);
        be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType transaction = FolderUtil.getTransaction(folderType, CDTRANSACTIONvalues.MEDICATIONSCHEME);
        log.debug("Setting version on outgoing PutTransactionSet message: " + latestVersion);
        transaction.setVersion(latestVersion);

        return kmehrmessageTemplate;

    }

    private Kmehrmessage fillSchemeIdentifier(Patient patient, Kmehrmessage kmehrmessageTemplate) throws Exception {

        IDKMEHR schemeIdentifier = getSchemeIdentifier(patient);

        if (schemeIdentifier == null) {
            return kmehrmessageTemplate;
        }

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessageTemplate);
        be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType transaction = FolderUtil.getTransaction(folderType, CDTRANSACTIONvalues.MEDICATIONSCHEME);

        IDKmehrUtil.removeIDKmehrs(transaction.getIds(), IDKMEHRschemes.LOCAL);
        ArrayList<IDKMEHR> idkmehrs = new ArrayList<>();
        idkmehrs.add(schemeIdentifier);
        log.debug("Setting local identifier on MS transaction of outgoing PutTransactionSet message: " + schemeIdentifier.getValue());
        IDKmehrUtil.addIDKmehrs(transaction.getIds(), idkmehrs);

        return kmehrmessageTemplate;

    }

    private String getLatestVersion(Patient patient) throws VitalinkException, GatewaySpecificErrorException {

        GetTransactionSetResponse transactionSet;
        try {
            transactionSet = hubFlow.getTransactionSet(patient.getId(), TRANSACTION_TYPE);
        } catch (TransactionNotFoundException e) {
            return null;
        }

        FolderType folderType = KmehrMessageUtil.getFolderType(transactionSet.getKmehrmessage());
        be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType transaction = FolderUtil.getTransaction(folderType, CDTRANSACTIONvalues.MEDICATIONSCHEME);
        return transaction.getVersion();

    }

    private IDKMEHR getSchemeIdentifier(Patient patient) throws VitalinkException, GatewaySpecificErrorException {

        GetTransactionListResponse getTransactionListResponse;

        try {
            getTransactionListResponse = hubFlow.getTransactionList(patient.getId(), TRANSACTION_TYPE);
        } catch (TransactionNotFoundException e) {
            return null;
        }

        TransactionSummaryType latestTransaction = TransactionHelper.findLatestTransaction(getTransactionListResponse, TRANSACTION_TYPE).orElse(null);
        if (latestTransaction == null) {
            return null;
        }

        List<IDKMEHR> idKmehrs = IDKmehrUtil.getIDKmehrs(latestTransaction.getIds(), IDKMEHRschemes.LOCAL);
        if (CollectionsUtil.notEmptyOrNull(idKmehrs)) {
            if (CollectionsUtil.size(idKmehrs) > 1) {
                log.warn("Found multiple local ids in transaction summary of gettransactionlist response for " + TRANSACTION_TYPE + ". Will assume the first one is the correct one.");
            }
            return idKmehrs.get(0);
        }

        return null;

    }

}
