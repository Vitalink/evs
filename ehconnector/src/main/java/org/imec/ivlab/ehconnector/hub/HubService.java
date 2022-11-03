package org.imec.ivlab.ehconnector.hub;

import static org.imec.ivlab.ehconnector.hub.logging.Kind.DECRYPTED;

import be.ehealth.business.intrahubcommons.exception.IntraHubBusinessConnectorException;
import be.ehealth.businessconnector.hubv3.session.HubSessionServiceFactory;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.AcknowledgeType;
import be.fgov.ehealth.hubservices.core.v3.GetLatestUpdateResponse;
import be.fgov.ehealth.hubservices.core.v3.GetPatientAuditTrailResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionListResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionSetResponse;
import be.fgov.ehealth.hubservices.core.v3.LocalSearchType;
import be.fgov.ehealth.hubservices.core.v3.PatientIdType;
import be.fgov.ehealth.hubservices.core.v3.PutTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.PutTransactionSetResponse;
import be.fgov.ehealth.hubservices.core.v3.RevokeTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.SelectGetLatestUpdateType;
import be.fgov.ehealth.hubservices.core.v3.SelectGetPatientAuditTrailType;
import be.fgov.ehealth.hubservices.core.v3.TransactionBaseType;
import be.fgov.ehealth.hubservices.core.v3.TransactionWithPeriodType;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDERROR;
import be.fgov.ehealth.standards.kmehr.schema.v1.ErrorType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.config.EVSConfig;
import org.imec.ivlab.core.config.EVSProperties;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.ehconnector.business.medicationscheme.hubhelper.HubConfigRsb;
import org.imec.ivlab.ehconnector.business.medicationscheme.hubhelper.HubConfigRsw;
import org.imec.ivlab.ehconnector.business.medicationscheme.hubhelper.HubConfigVitalink;
import org.imec.ivlab.ehconnector.hub.exception.GatewayListOfErrorsException;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.exception.curable.Curable;
import org.imec.ivlab.ehconnector.hub.exception.curable.InternalErrorException;
import org.imec.ivlab.ehconnector.hub.exception.curable.SocketTimeoutException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.InvalidNumberOfDataEntriesAvailableWithinRequestException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.InvalidTherapeuticRelationException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.NoConsentGivenException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.NoDataIsAvailableForProfileOrCriteria;
import org.imec.ivlab.ehconnector.hub.exception.incurable.NoNodeFoundMatchingTheURI;
import org.imec.ivlab.ehconnector.hub.exception.incurable.RemoveNotAllowedIfDataEntryAlreadyDeleted;
import org.imec.ivlab.ehconnector.hub.exception.incurable.SubjectWithSSINUnknownException;
import org.imec.ivlab.ehconnector.hub.logging.Kind;
import org.imec.ivlab.ehconnector.hub.logging.MessageWriter;

public class HubService {

    private final static Logger log = LogManager.getLogger(HubService.class);

    private be.ehealth.businessconnector.hubv3.session.HubService hubService;

    private static Map<String, Class<? extends GatewaySpecificErrorException>> knownExceptions = new LinkedHashMap<>();

    static {
        knownExceptions.put(InternalErrorException.errorCode, InternalErrorException.class);
        knownExceptions.put(SubjectWithSSINUnknownException.errorCode, SubjectWithSSINUnknownException.class);
        knownExceptions.put(NoNodeFoundMatchingTheURI.errorCode, NoNodeFoundMatchingTheURI.class);
        knownExceptions.put(NoDataIsAvailableForProfileOrCriteria.errorCode, NoDataIsAvailableForProfileOrCriteria.class);
        knownExceptions.put(InvalidTherapeuticRelationException.errorCode, InvalidTherapeuticRelationException.class);
        knownExceptions.put(NoConsentGivenException.errorCode, NoConsentGivenException.class);
        knownExceptions.put(InvalidNumberOfDataEntriesAvailableWithinRequestException.errorCode, InvalidNumberOfDataEntriesAvailableWithinRequestException.class);
        knownExceptions.put(RemoveNotAllowedIfDataEntryAlreadyDeleted.errorCode, RemoveNotAllowedIfDataEntryAlreadyDeleted.class);
    }

    public HubService() {

        initHubConfig();

        try {
            hubService = HubSessionServiceFactory.getHubService();
        } catch (ConnectorException e) {
            throw new RuntimeException(e);
        }

    }

    public GetTransactionListResponse getTransactionList(PatientIdType patientIdType, LocalSearchType localSearchType, TransactionWithPeriodType transactionWithPeriodType) throws VitalinkException, GatewaySpecificErrorException {

        try {
            return getTransactionListOnce(patientIdType, localSearchType, transactionWithPeriodType);
        } catch (Exception e) {
            if (e instanceof Curable) {
                log.error("Error during execution of GetTransactionList operation: " + e.getMessage());
                return getTransactionList(patientIdType, localSearchType, transactionWithPeriodType);
            } else {
                throw e;
            }
        }

    }

    public GetLatestUpdateResponse getLatestUpdate(SelectGetLatestUpdateType selectGetLatestUpdateType) throws VitalinkException, GatewaySpecificErrorException {

        try {
            return getLatestUpdateOnce(selectGetLatestUpdateType);
        } catch (Exception e) {
            if (e instanceof Curable) {
                log.error("Error during execution of GetLatestUpdate operation: " + e.getMessage());
                return getLatestUpdate(selectGetLatestUpdateType);
            } else {
                throw e;
            }
        }

    }

    public GetPatientAuditTrailResponse getPatientAuditTrail(SelectGetPatientAuditTrailType selectGetPatientAuditTrailType) throws GatewaySpecificErrorException, VitalinkException {

        try {
            return getPatientAuditTrailOnce(selectGetPatientAuditTrailType);
        } catch (Exception e) {
            if (e instanceof Curable) {
                log.error("Error during execution of GetPatientAuditTrail operation: " + e.getMessage());
                return getPatientAuditTrailOnce(selectGetPatientAuditTrailType);
            } else {
                throw e;
            }
        }

    }

    public PutTransactionSetResponse putTransactionSet(Kmehrmessage kmehrmessage) throws VitalinkException, GatewaySpecificErrorException {

        try {
            return putTransactionSetOnce(kmehrmessage);
        } catch (Exception e) {
            if (e instanceof Curable) {
                log.error("Error during execution of PutTransactionSet operation: " + e.getMessage());
                return putTransactionSet(kmehrmessage);
            } else {
                throw e;
            }
        }

    }

    public PutTransactionResponse putTransaction(Kmehrmessage kmehrmessage) throws VitalinkException, GatewaySpecificErrorException {

        try {
            return putTransactionOnce(kmehrmessage);
        } catch (Exception e) {
            if (e instanceof Curable) {
                log.error("Error during execution of PutTransaction operation: " + e.getMessage());
                return putTransaction(kmehrmessage);
            } else {
                throw e;
            }
        }

    }

    public GetTransactionSetResponse getTransactionSet(PatientIdType patientIdType, TransactionBaseType transactionBaseType) throws VitalinkException, GatewaySpecificErrorException {

        return getTransactionSet(patientIdType, transactionBaseType, true);

    }

    public GetTransactionSetResponse getTransactionSet(PatientIdType patientIdType, TransactionBaseType transactionBaseType, boolean retryOnCurableError) throws VitalinkException, GatewaySpecificErrorException {

        try {
            return getTransactionSetOnce(patientIdType, transactionBaseType);
        } catch (Exception e) {
            if (retryOnCurableError && e instanceof Curable) {
                log.error("Error during execution of GetTransactionSet operation.", e);
                return getTransactionSet(patientIdType, transactionBaseType);
            } else {
                throw e;
            }
        }

    }

    public RevokeTransactionResponse revokeTransaction(PatientIdType patientIdType, TransactionBaseType transactionBaseType) throws VitalinkException, GatewaySpecificErrorException {

        try {
            return revokeTransactionOnce(patientIdType, transactionBaseType);
        } catch (Exception e) {
            if (e instanceof Curable) {
                log.error("Error during execution of RevokeTransaction operation: " + e.getMessage());
                return revokeTransaction(patientIdType, transactionBaseType);
            } else {
                throw e;
            }
        }

    }

    public GetTransactionResponse getTransaction(PatientIdType patientIdType, TransactionBaseType transactionBaseType) throws VitalinkException, GatewaySpecificErrorException {

        try {
            return getTransactionOnce(patientIdType, transactionBaseType);
        } catch (Exception e) {
            if (e instanceof Curable) {
                log.error("Error during execution of GetTransaction operation: " + e.getMessage());
                return getTransaction(patientIdType, transactionBaseType);
            } else {
                throw e;
            }
        }

    }

    public GetTransactionListResponse getTransactionListOnce(PatientIdType patientIdType, LocalSearchType localSearchType, TransactionWithPeriodType transactionWithPeriodType) throws VitalinkException, GatewaySpecificErrorException {

        try {
            GetTransactionListResponse transactionListResponse = hubService.getTransactionList(patientIdType, localSearchType, transactionWithPeriodType);
            if (transactionListResponse.getKmehrheader() == null || transactionListResponse.getKmehrheader().getFolder() == null || CollectionsUtil.emptyOrNull(transactionListResponse.getKmehrheader().getFolder().getTransactions())) {
                verifyResponse(transactionListResponse.getAcknowledge());
            }
            return transactionListResponse;
        } catch (TechnicalConnectorException | IntraHubBusinessConnectorException | GatewayListOfErrorsException e) {
            verifyException(e);
            throw new VitalinkException(e);
        }

    }

    private PutTransactionSetResponse putTransactionSetOnce(Kmehrmessage kmehrmessage) throws VitalinkException, GatewaySpecificErrorException {
        try {
            writeKmehrmessage(kmehrmessage, "PutTransactionSetRequest-kmehrmessage", DECRYPTED);
            PutTransactionSetResponse putTransactionSetResponse = hubService.putTransactionSet(kmehrmessage);
            verifyResponse(putTransactionSetResponse.getAcknowledge());
            return putTransactionSetResponse;
        } catch (TechnicalConnectorException | IntraHubBusinessConnectorException | GatewayListOfErrorsException e) {
            verifyException(e);
            throw new VitalinkException(e);
        }

    }

    private void writeKmehrmessage(Kmehrmessage kmehrmessage, String operation, Kind kind) {
        MessageWriter.logMessage(kmehrToString(kmehrmessage), operation, kind);
    }

    private String kmehrToString(Kmehrmessage kmehrmessage) {
        if (kmehrmessage == null) {
            return null;
        }
        try {
            return JAXBUtils.marshal(kmehrmessage);
        } catch (JAXBException e) {
            log.error("Failed to intepret kmehr message", e);
            return null;
        }
    }

    private PutTransactionResponse putTransactionOnce(Kmehrmessage kmehrmessage) throws VitalinkException, GatewaySpecificErrorException {
        try {
            writeKmehrmessage(kmehrmessage, "PutTransactionRequest-kmehrmessage", DECRYPTED);
            PutTransactionResponse putTransactionSetResponse = hubService.putTransaction(kmehrmessage);
            verifyResponse(putTransactionSetResponse.getAcknowledge());
            return putTransactionSetResponse;
        } catch (TechnicalConnectorException | IntraHubBusinessConnectorException | GatewayListOfErrorsException e) {
            verifyException(e);
            throw new VitalinkException(e);
        }

    }

    private RevokeTransactionResponse revokeTransactionOnce(PatientIdType patientIdType, TransactionBaseType transactionBaseType) throws VitalinkException, GatewaySpecificErrorException {
        try {
            RevokeTransactionResponse revokeTransactionResponse = hubService.revokeTransaction(patientIdType, transactionBaseType);
            verifyResponse(revokeTransactionResponse.getAcknowledge());
            return revokeTransactionResponse;
        } catch (TechnicalConnectorException | IntraHubBusinessConnectorException | GatewayListOfErrorsException e) {
            verifyException(e);
            throw new VitalinkException(e);
        }

    }

    private GetPatientAuditTrailResponse getPatientAuditTrailOnce(SelectGetPatientAuditTrailType selectGetPatientAuditTrailType) throws GatewaySpecificErrorException, VitalinkException {

        try {
            GetPatientAuditTrailResponse patientAuditTrailResponse = hubService.getPatientAuditTrail(selectGetPatientAuditTrailType);
            verifyResponse(patientAuditTrailResponse.getAcknowledge());
            return patientAuditTrailResponse;
        } catch (TechnicalConnectorException | IntraHubBusinessConnectorException | GatewayListOfErrorsException e) {
            verifyException(e);
            throw new VitalinkException(e);
        }

    }

    private GetTransactionSetResponse getTransactionSetOnce(PatientIdType patientIdType, TransactionBaseType transactionBaseType) throws VitalinkException, GatewaySpecificErrorException {

        try {
            GetTransactionSetResponse getTransactionSetResponse = hubService.getTransactionSet(patientIdType, transactionBaseType);
            writeKmehrmessage(getTransactionSetResponse.getKmehrmessage(), "GetTransactionSetResponse-kmehrmessage", DECRYPTED);
            verifyResponse(getTransactionSetResponse.getAcknowledge());
            return getTransactionSetResponse;
        } catch (TechnicalConnectorException | IntraHubBusinessConnectorException | GatewayListOfErrorsException e) {
            verifyException(e);
            throw new VitalinkException(e);
        }

    }

    private GetTransactionResponse getTransactionOnce(PatientIdType patientIdType, TransactionBaseType transactionBaseType) throws VitalinkException, GatewaySpecificErrorException {

        try {
            GetTransactionResponse getTransactionResponse = hubService.getTransaction(patientIdType, transactionBaseType);
            writeKmehrmessage(getTransactionResponse.getKmehrmessage(), "GetTransactionResponse-kmehrmessage", DECRYPTED);
            verifyResponse(getTransactionResponse.getAcknowledge());
            return getTransactionResponse;
        } catch (TechnicalConnectorException | IntraHubBusinessConnectorException | GatewayListOfErrorsException e) {
            verifyException(e);
            throw new VitalinkException(e);
        }

    }

    private GetLatestUpdateResponse getLatestUpdateOnce(SelectGetLatestUpdateType selectGetLatestUpdateType) throws VitalinkException, GatewaySpecificErrorException {
        try {
            GetLatestUpdateResponse latestUpdateResponse = hubService.getLatestUpdate(selectGetLatestUpdateType);
            if (CollectionUtils.isEmpty(latestUpdateResponse.getLatestupdatelist().getLatestupdates())) {
                verifyResponse(latestUpdateResponse.getAcknowledge());
            }
            return latestUpdateResponse;
        } catch (TechnicalConnectorException | IntraHubBusinessConnectorException | GatewayListOfErrorsException e) {
            verifyException(e);
            throw new VitalinkException(e);
        }

    }

    private GatewayListOfErrorsException.Error findError(List<GatewayListOfErrorsException.Error> errors, String code) {
        if (CollectionsUtil.emptyOrNull(errors)) {
            return null;
        }

        for (GatewayListOfErrorsException.Error error : errors) {
            if (StringUtils.equals(error.getStatusCode(), code)) {
                return error;
            }
        }

        return null;

    }

    private void verifyException(Exception e) throws SocketTimeoutException {

        if (StringUtils.containsIgnoreCase(ExceptionUtils.getMessage(e), "java.net.SocketTimeoutException")) {
            throw new SocketTimeoutException("Got a socket timeout exception when connecting", e);
        }

    }

    private List<ErrorType> getRealErrors(AcknowledgeType acknowledgeType) {

        List<ErrorType> errors = new ArrayList<>();

        if (acknowledgeType == null || acknowledgeType.getErrors() == null) {
            return errors;
        }


        for (ErrorType errorType : acknowledgeType.getErrors()) {

            if (errorType.getCds() == null) {
                continue;
            }

            for (CDERROR cderror : errorType.getCds()) {
                if (!cderror.getValue().equals("200") && !cderror.getValue().equals("202") && !StringUtils.equalsIgnoreCase(cderror.getValue(), "VALID")) {
                    errors.add(errorType);
                }
            }

        }

        return errors;
    }

    private void verifyResponse(AcknowledgeType acknowledgeType) throws GatewaySpecificErrorException, GatewayListOfErrorsException {

        // Temporarily NOT check for iscomplete, see VAZG-148, since sometimes value 'true' is returned in case of errors, for instance pharmacist not having access to SUMEHR transaction types
//        if (acknowledgeType.isIscomplete()) {
//            return;
//        }

        // Temporarily check for statuscode. If no statuscode != 200, then request can be considered as successfull.
        // Needs to be reverted ASAP because might not work for RSW. But then first VAZG-148 needs to be fixed.
        if (acknowledgeType.isIscomplete()) {

            if (CollectionsUtil.emptyOrNull(getRealErrors(acknowledgeType))) {
                return;
            }

        }


        if (CollectionsUtil.emptyOrNull(acknowledgeType.getErrors())) {
            return;
        }

        GatewayListOfErrorsException genericGatewayException = new GatewayListOfErrorsException(acknowledgeType.getErrors());

        if (knownExceptions.size() > 0) {
            for (Map.Entry<String, Class<? extends GatewaySpecificErrorException>> codeAndException : knownExceptions.entrySet()) {
                GatewayListOfErrorsException.Error matchingError = findError(genericGatewayException.getErrors(), codeAndException.getKey());
                if (matchingError == null) {
                    continue;
                }

                Class<? extends GatewaySpecificErrorException> aClass = codeAndException.getValue();
                Constructor<? extends GatewaySpecificErrorException> constructor = null;
                try {
                    constructor = aClass.getConstructor(String.class);
                    GatewaySpecificErrorException exception = constructor.newInstance(matchingError.getMessage());
                    throw exception;
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

            }

        }

        throw genericGatewayException;

    }

    private void initHubConfig() {

        try {

            String property = EVSConfig.getInstance().getProperty(EVSProperties.CHOSEN_HUB);
            if (StringUtils.equalsIgnoreCase(property, "vitalink")) {
                HubConfigVitalink.initConfig();
            } else if (StringUtils.equalsIgnoreCase(property, "rsw")) {
                HubConfigRsw.initConfig();
            } else if (StringUtils.equalsIgnoreCase(property, "rsb")) {
                HubConfigRsb.initConfig();
            } else {
                throw new RuntimeException("No or invalid hub configured. Configured hub: " + property);
            }

        } catch (TechnicalConnectorException e) {
            log.error("Error when setting vitalink hub config.", e);
        }
    }



}
