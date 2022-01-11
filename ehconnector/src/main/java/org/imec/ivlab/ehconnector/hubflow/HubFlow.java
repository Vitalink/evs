package org.imec.ivlab.ehconnector.hubflow;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.FolderTypeUnbounded;
import be.fgov.ehealth.hubservices.core.v3.GetLatestUpdateResponse;
import be.fgov.ehealth.hubservices.core.v3.GetPatientAuditTrailResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionListResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionSetResponse;
import be.fgov.ehealth.hubservices.core.v3.KmehrHeaderGetTransactionList;
import be.fgov.ehealth.hubservices.core.v3.LocalSearchType;
import be.fgov.ehealth.hubservices.core.v3.PatientIdType;
import be.fgov.ehealth.hubservices.core.v3.PutTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.PutTransactionSetResponse;
import be.fgov.ehealth.hubservices.core.v3.RevokeTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.TransactionBaseType;
import be.fgov.ehealth.hubservices.core.v3.TransactionSummaryType;
import be.fgov.ehealth.hubservices.core.v3.TransactionWithPeriodType;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.xml.bind.JAXBException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.config.EVSConfig;
import org.imec.ivlab.core.config.EVSProperties;
import org.imec.ivlab.core.exceptions.DataNotFoundException;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.HCPartyUtil;
import org.imec.ivlab.core.kmehr.model.util.IDKmehrUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.ehconnector.business.HubHelper;
import org.imec.ivlab.ehconnector.hub.HubService;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.LatestUpdateNotFoundException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.NoDataIsAvailableForProfileOrCriteria;
import org.imec.ivlab.ehconnector.hub.exception.incurable.NoNodeFoundMatchingTheURI;
import org.imec.ivlab.ehconnector.hub.exception.incurable.SubjectWithSSINUnknownException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.TransactionNotFoundException;
import org.imec.ivlab.ehconnector.hub.util.AuthorUtil;
import org.imec.ivlab.ehconnector.util.TransactionHelper;

public class HubFlow {

    private final static Logger LOG = LogManager.getLogger(HubFlow.class);

    private LocalSearchType localSearchType;
    private HubService hubService;
    private HubHelper helper;

    private boolean retryOnCurableError = true;


    public HubFlow() {
        hubService = new HubService();
        helper = new HubHelper();

        localSearchType = LocalSearchType.fromValue(StringUtils.lowerCase(EVSConfig.getInstance().getProperty(EVSProperties.SEARCH_TYPE)));
    }


    public PutTransactionResponse putTransaction(Kmehrmessage kmehrMessage) throws VitalinkException, GatewaySpecificErrorException, TechnicalConnectorException {

        LOG.debug("Started putTransaction");

        generateHeaderSender(kmehrMessage);
        generateFirstTransactionAuthorBasedOnCertificate(kmehrMessage);
        PutTransactionResponse putTransactionResponse = hubService.putTransaction(kmehrMessage);

        LOG.debug("Completed putTransaction");

        return putTransactionResponse;

    }

    public RevokeTransactionResponse revokeTransaction(String patientId, Kmehrmessage kmehrMessage) throws VitalinkException, GatewaySpecificErrorException, TechnicalConnectorException {

        LOG.debug("Started revokeTransaction");

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrMessage);
        if (folderType == null || CollectionsUtil.emptyOrNull(folderType.getTransactions())) {
            throw new RuntimeException("No transaction provided in kmehrmessage!");
        }
        IDKMEHR idKmehr = getFirstLocalIdKmehrWithSL(folderType.getTransactions().get(0).getIds());

        TransactionBaseType transactionBaseType = helper.createTransactionBaseType(idKmehr, null);
        PatientIdType patientIdType = helper.createPatientIdType(patientId);

        generateHeaderSender(kmehrMessage);
        generateFirstTransactionAuthorBasedOnCertificate(kmehrMessage);
        RevokeTransactionResponse revokeTransactionResponse = hubService.revokeTransaction(patientIdType, transactionBaseType);

        LOG.debug("Completed revokeTransaction");

        return revokeTransactionResponse;

    }

    private void generateFirstTransactionAuthorBasedOnCertificate(Kmehrmessage kmehrMessage) throws TechnicalConnectorException {
        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrMessage);
        be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType firstTransaction = FolderUtil.getFirstTransaction(folderType);
        AuthorUtil.regenerateTransactionAuthorBasedOnCertificate(firstTransaction);
    }

    private IDKMEHR getFirstLocalIdKmehrWithSL(List<IDKMEHR> idkmehrs) {

        IDKMEHR idkmehr = null;

        List<IDKMEHR> localIdKmehrs = IDKmehrUtil.getIDKmehrs(idkmehrs, IDKMEHRschemes.LOCAL);

        if (CollectionsUtil.notEmptyOrNull(localIdKmehrs)) {

            for (IDKMEHR localIdKmehr : localIdKmehrs) {
                if (StringUtils.isNotEmpty(localIdKmehr.getSL())) {
                    idkmehr = localIdKmehr;
                    break;
                }
            }

        }

        if (idkmehr == null) {
            throw new DataNotFoundException("No LOCAL IDKMEHR found with an SL attribute");
        }

        return idkmehr;

    }

    public List<GetTransactionResponse> getIndividualTransactions(String patientId, TransactionType transactionType) throws VitalinkException, GatewaySpecificErrorException {

        List<GetTransactionResponse> getTransactionResponses = new ArrayList<>();

        GetTransactionListResponse transactionList = getTransactionList(patientId, transactionType);

        List<TransactionSummaryType> transactions = Optional.ofNullable(transactionList)
            .map(GetTransactionListResponse::getKmehrheader)
            .map(KmehrHeaderGetTransactionList::getFolder)
            .map(FolderTypeUnbounded::getTransactions)
            .orElse(Collections.emptyList());

        for (TransactionSummaryType transactionSummaryType : transactions) {

            List<IDKMEHR> idKmehrs = IDKmehrUtil.getIDKmehrs(transactionSummaryType.getIds(), IDKMEHRschemes.LOCAL);
            HcpartyType hubAuthor = findHubAuthor(transactionSummaryType);
            String transactionIdentification=TransactionHelper.getTransactionDetails(transactionSummaryType);
            TransactionBaseType transactionBaseType = helper.createTransactionBaseType(idKmehrs.get(0), hubAuthor);

            LOG.debug("Started getTransaction for " + transactionIdentification);
            GetTransactionResponse transaction = null;
            try {
                transaction = hubService.getTransaction(helper.createPatientIdType(patientId), transactionBaseType);
            } catch (VitalinkException | GatewaySpecificErrorException e) {
                LOG.error("Error:", e);
            }
            getTransactionResponses.add(transaction);
            LOG.debug("Completed getTransaction");

        }
        if(getTransactionResponses.size() != transactions.size() ) {
            LOG.error("ATTENTION: At least one of the transactions failed.");
        }
        return getTransactionResponses;

    }

    public PutTransactionSetResponse putTransactionSet(String patientId, TransactionType transactionType, Kmehrmessage kmehrMessage) throws VitalinkException, GatewaySpecificErrorException, TechnicalConnectorException {

        generateHeaderSender(kmehrMessage);
        addHubAuthorToMSTransaction(patientId, transactionType, kmehrMessage);

        LOG.debug("Started putTransactionSet");

        try {
            PutTransactionSetResponse putTransactionSetResponse = hubService.putTransactionSet(kmehrMessage);
            LOG.debug("Completed putTransactionSet");
            return putTransactionSetResponse;
        } catch (Throwable t) {
            throw t;
        }
    }

    private void generateHeaderSender(Kmehrmessage kmehrmessage) throws TechnicalConnectorException {
        AuthorUtil.regenerateSenderAuthorBasedOnCertificate(kmehrmessage.getHeader());
    }

    private void addHubAuthorToMSTransaction(String patientId, TransactionType transactionType, Kmehrmessage kmehrMessage) throws VitalinkException, GatewaySpecificErrorException {
        HcpartyType hubAuthor = findHubAuthor(patientId, transactionType);
        if (hubAuthor != null) {
            FolderType folder = KmehrMessageUtil.getFolderType(kmehrMessage);
            be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType msTransaction = FolderUtil.getTransaction(folder, CDTRANSACTIONvalues.MEDICATIONSCHEME);
            msTransaction.getAuthor().getHcparties().add(0,hubAuthor);
            LOG.debug("hubHcParty added from GetTransactionList.");
        } else {
            LOG.debug("No hubHcParty returned in GetTransactionList.");
        }
    }

    private HcpartyType findHubAuthor(String patientId, TransactionType transactionType) throws VitalinkException, GatewaySpecificErrorException {
        HcpartyType hubHcParty = null;
        try {
            GetTransactionListResponse transactionListResponse = getTransactionList(patientId, transactionType);
            Optional<TransactionSummaryType> latestTransaction = TransactionHelper.findLatestTransaction(transactionListResponse,transactionType);
            List<HcpartyType> hcpartyTypes = latestTransaction
                .map(TransactionSummaryType::getAuthor)
                .map(AuthorType::getHcparties)
                .orElse(null);
            hubHcParty = HCPartyUtil.findHubHcParty(hcpartyTypes);
        } catch (TransactionNotFoundException e) {
            LOG.debug(String.format("No transaction was found in getTransactionListResponse for patient %s and transactiontype %s", patientId, transactionType));
        }
        return hubHcParty;
    }


    public GetTransactionSetResponse getTransactionSet(String patientId, TransactionType transactionType, List<CDTRANSACTION> getCdtransactionsFilter) throws VitalinkException, GatewaySpecificErrorException {

        GetTransactionListResponse transactionListResponse = getTransactionList(patientId, transactionType);

        TransactionSummaryType transactionSummaryType = TransactionHelper.findLatestTransaction(transactionListResponse, transactionType).orElseThrow(() -> new DataNotFoundException("No "
            + "transactionsummary found for transactionType " + transactionType + " and patient " + patientId));
        List<IDKMEHR> idKmehrs = IDKmehrUtil.getIDKmehrs(transactionSummaryType.getIds(), IDKMEHRschemes.LOCAL);
        IDKMEHR transactionId = idKmehrs.get(0);
        HcpartyType hubAuthor = findHubAuthor(transactionSummaryType);

        TransactionBaseType transactionBaseType = helper.createTransactionBaseType(transactionId, hubAuthor);

        if (CollectionsUtil.notEmptyOrNull(getCdtransactionsFilter)) {
            transactionBaseType.getCds().addAll(getCdtransactionsFilter);
        }
        LOG.debug("Started getTransactionSet");
        GetTransactionSetResponse transactionSet = hubService.getTransactionSet(helper.createPatientIdType(patientId), transactionBaseType, isRetryOnCurableError());
        LOG.debug("Completed getTransactionSet");
        return transactionSet;

    }


    private HcpartyType findHubAuthor(TransactionSummaryType transactionSummaryType) {
        return Optional.ofNullable(transactionSummaryType.getAuthor())
            .map(AuthorType::getHcparties)
            .orElse(Collections.emptyList())
            .stream()
            .filter(HCPartyUtil::isHubHcParty)
            .findFirst()
            .orElse(null);
    }


    public GetTransactionSetResponse getTransactionSet(String patientId, TransactionType transactionType) throws VitalinkException, GatewaySpecificErrorException {
        return getTransactionSet(patientId, transactionType, null);
    }

    public GetPatientAuditTrailResponse getPatientAuditTrail(String patientId) throws GatewaySpecificErrorException, VitalinkException {

        LOG.debug("Started getPatientAuditTrail");

        GetPatientAuditTrailResponse patientAuditTrailResponse = hubService.getPatientAuditTrail(helper.createGetPatientAuditTrailType(patientId));

        LOG.debug("Completed getPatientAuditTrail");

        return patientAuditTrailResponse;

    }

    public GetLatestUpdateResponse getLatestUpdate(String patientId, TransactionType transactionType) throws VitalinkException, GatewaySpecificErrorException {

//        LOG.debug("Started getLatestUpdate");
        String transactiontypeString = transactionType.getTransactionTypeValueForGetLatestUpdate();

        try {
            GetLatestUpdateResponse getLatestUpdateResponse = hubService.getLatestUpdate(helper.createSelectGetLatestUpdateType(patientId, transactiontypeString));
            if (getLatestUpdateResponse.getLatestupdatelist() == null || CollectionsUtil.emptyOrNull(getLatestUpdateResponse.getLatestupdatelist().getLatestupdates())) {
                throw new LatestUpdateNotFoundException("No latestupdate found for patient " + patientId + " and transactiontype: " + transactiontypeString);
            }
            return getLatestUpdateResponse;
        } catch (NoNodeFoundMatchingTheURI | SubjectWithSSINUnknownException e) {
            throw new LatestUpdateNotFoundException("No latestupdate found for patient " + patientId + " and transactiontype: " + transactiontypeString);
        }

//        LOG.debug("Completed getLatestUpdate");

    }

    public GetTransactionListResponse getTransactionList(String patientId, TransactionType transactionType) throws VitalinkException, GatewaySpecificErrorException {

//        LOG.debug("Started getTransactionList");

        PatientIdType patientIdType = helper.createPatientIdType(patientId);
        TransactionWithPeriodType transactionWithPeriodType = helper.createTransactionWithPeriodType(transactionType);

        try {
            GetTransactionListResponse transactionList = hubService.getTransactionList(patientIdType, localSearchType, transactionWithPeriodType);
            if (!TransactionHelper.findLatestTransaction(transactionList, transactionType).isPresent()) {
                throw new TransactionNotFoundException("No transaction " + transactionType.getTransactionTypeValueForGetTransactionList() + " found for patient " + patientId + " using filters: " + objectsToString(patientId, localSearchType, transactionWithPeriodType));
            }
            return transactionList;

        } catch (NoDataIsAvailableForProfileOrCriteria | SubjectWithSSINUnknownException | NoNodeFoundMatchingTheURI e) {
            throw new TransactionNotFoundException("No transaction " + transactionType.getTransactionTypeValueForGetTransactionList() + " found for patient " + patientId + " using filters: " + objectsToString(patientId, localSearchType, transactionWithPeriodType));
        }

//        LOG.debug("Completed getTransactionList");



    }

    private String objectsToString(Object... objects) {

        StringBuilder stringBuilder = new StringBuilder();

        for (Object object : objects) {
            try {
                if (object instanceof String) {
                    stringBuilder.append((String) object);
                } else {
                    stringBuilder.append(System.lineSeparator()).append(JAXBUtils.marshal(object));
                }
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();

    }

    public boolean isRetryOnCurableError() {
        return retryOnCurableError;
    }

    public void setRetryOnCurableError(boolean retryOnCurableError) {
        this.retryOnCurableError = retryOnCurableError;
    }
}
