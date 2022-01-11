package org.imec.ivlab.ehconnector.hubflow;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.AcknowledgeType;
import be.fgov.ehealth.hubservices.core.v3.GetLatestUpdateResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionSetResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;
import org.imec.ivlab.core.config.EVSConfig;
import org.imec.ivlab.core.config.EVSProperties;
import org.imec.ivlab.core.data.PatientKey;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.patient.PatientReader;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.session.SessionManager;

public class HubFlowTestRunner {

    private final static Logger LOG = LogManager.getLogger(HubFlowTestRunner.class);


    public static void main(String[] args) throws GatewaySpecificErrorException, VitalinkException, TechnicalConnectorException {
        HubFlowTestRunner testRunner = new HubFlowTestRunner();
        testRunner.testGetTransactionSet();
    }


    public void testGetTransactionSet() throws TechnicalConnectorException, GatewaySpecificErrorException, VitalinkException {
        EVSConfig.getInstance().setProperty(EVSProperties.CHOSEN_HUB, "VITALINK");
        EVSConfig.getInstance().setProperty(EVSProperties.SEARCH_TYPE, "GLOBAL");
        EVSConfig.getInstance().setProperty(EVSProperties.FILTER_OUT_TRANSACTIONS_HAVING_PATIENT_ACCESS_NO, "false");
        HubFlow hubFlow = new HubFlow();

        SessionManager.connectWith(AuthenticationConfigReader.loadByName(AuthenticationConfigReader.GP_EXAMPLE));
        PatientKey patientKey = PatientKey.PATIENT_EXAMPLE;
        GetTransactionSetResponse transactionSetResponse = hubFlow.getTransactionSet(PatientReader.loadPatientByKey(patientKey).getId(), TransactionType.MEDICATION_SCHEME);
        LOG.info(transactionSetResponse);

    }

    public void testGetLatestUpdate() throws TechnicalConnectorException, GatewaySpecificErrorException, VitalinkException {

        HubFlow hubFlow = new HubFlow();

        SessionManager.connectWith(AuthenticationConfigReader.loadByName(AuthenticationConfigReader.GP_EXAMPLE));
        GetLatestUpdateResponse getLatestUpdateResponse = hubFlow.getLatestUpdate(PatientReader.loadPatientByKey(PatientKey.PATIENT_EXAMPLE).getId(), TransactionType.MEDICATION_SCHEME);
        AcknowledgeType acknowledge = getLatestUpdateResponse.getAcknowledge();

    }

}