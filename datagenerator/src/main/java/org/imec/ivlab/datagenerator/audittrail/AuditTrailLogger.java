package org.imec.ivlab.datagenerator.audittrail;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.GetPatientAuditTrailResponse;
import be.fgov.ehealth.hubservices.core.v3.Transactionaccess;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;
import org.imec.ivlab.core.data.PatientKey;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.patient.PatientReader;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.datagenerator.uploader.UploaderRunner;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.session.SessionManager;
import org.imec.ivlab.ehconnector.hubflow.HubFlow;

public class AuditTrailLogger {

    private final static Logger LOG = LogManager.getLogger(UploaderRunner.class);
    private HubFlow hubFlow;

    public static void main(String[] args) throws GatewaySpecificErrorException, VitalinkException, TechnicalConnectorException {
        AuditTrailLogger auditTrailLogger = new AuditTrailLogger();
        auditTrailLogger.logPatientAuditTrail(PatientReader.loadPatientByKey(PatientKey.PATIENT_EXAMPLE), AuthenticationConfigReader.GP_EXAMPLE);
    }

    public AuditTrailLogger() {
        hubFlow = new HubFlow();
    }

    public void logPatientAuditTrail(Patient patient, String actorId) throws GatewaySpecificErrorException, VitalinkException, TechnicalConnectorException {

        SessionManager.connectWith(AuthenticationConfigReader.loadByName(actorId));

        GetPatientAuditTrailResponse patientAuditTrail = hubFlow.getPatientAuditTrail(patient.getId());

        if (patientAuditTrail == null || patientAuditTrail.getTransactionaccesslist() == null || patientAuditTrail.getTransactionaccesslist().getTransactionaccesses() == null) {
            LOG.info("No audit trail found for patient: " + patient.inReadableFormat());
        }

        for (Transactionaccess transactionaccess : patientAuditTrail.getTransactionaccesslist().getTransactionaccesses()) {
            LOG.info("------------- Audit trail entry -------------");
            LOG.info("accessed at: " + transactionaccess.getAccessdatetime());
            if (transactionaccess.getHcparties() != null) {
                for (HcpartyType hcpartyType : transactionaccess.getHcparties()) {
                    LOG.info("Accessed by: " + org.imec.ivlab.core.util.StringUtils.joinWith(" ", hcpartyType.getFirstname(), hcpartyType.getFamilyname(), hcpartyType.getName()));
                }
            }
            LOG.info("break the glass info: " + transactionaccess.getBreaktheglass());
            LOG.info("patient: " + transactionaccess.getPatient().getIds().get(0).getValue());
            LOG.info("transaction: " + transactionaccess.getTransaction().getIds().get(0).getValue());
        }


    }

}
