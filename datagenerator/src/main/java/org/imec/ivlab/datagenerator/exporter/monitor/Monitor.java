package org.imec.ivlab.datagenerator.exporter.monitor;

import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.GetLatestUpdateResponse;
import be.fgov.ehealth.hubservices.core.v3.Latestupdate;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;
import org.imec.ivlab.core.data.PatientKey;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.patient.PatientReader;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.exception.curable.Curable;
import org.imec.ivlab.ehconnector.hub.exception.incurable.InvalidConfigurationException;
import org.imec.ivlab.ehconnector.hub.session.SessionManager;
import org.imec.ivlab.ehconnector.hubflow.HubFlow;


public class Monitor {

    private final static Logger log = LogManager.getLogger(Monitor.class);

    private HubFlow hubFlow;

    private static long timeoutBetweenSafeChecksInMillis = 2500;

    public Monitor() {
         hubFlow = new HubFlow();
    }


    private void authenticate(String actorID) throws TechnicalConnectorException, InvalidConfigurationException {
        SessionManager.connectWith(AuthenticationConfigReader.loadByName(actorID));
    }

    private String parseVersion(GetLatestUpdateResponse latestUpdateResponse, TransactionType transactionType) {

        if (latestUpdateResponse == null || latestUpdateResponse.getLatestupdatelist() == null || CollectionsUtil.emptyOrNull(latestUpdateResponse.getLatestupdatelist().getLatestupdates())) {
            return null;
        }

        return latestUpdateResponse.getLatestupdatelist().getLatestupdates()
                            .stream()
                            .filter(latestUpdate -> latestUpdate.getCd() != null)
                            .filter(isCdTransaction())
                            .filter(matchesTransactionType(transactionType))
                            .map(Latestupdate::getVersion)
                            .findFirst()
                            .orElse(null);

    }

    private Predicate<Latestupdate> matchesTransactionType(TransactionType transactionType) {
        return latestUpdate -> StringUtils.equals(latestUpdate
            .getCd()
            .getValue(), transactionType.getTransactionTypeValueForGetLatestUpdate());
    }

    private Predicate<Latestupdate> isCdTransaction() {
        return latestUpdate -> StringUtils.equals(latestUpdate
            .getCd()
            .getS()
            .value(), CDTRANSACTIONschemes.CD_TRANSACTION.value());
    }

    public String waitForNewSafeContent(MonitorInstruction monitorInstruction, String lastKnownVersion) throws TechnicalConnectorException, GatewaySpecificErrorException, VitalinkException {

        do {

            try {

                authenticate(monitorInstruction.getActorID());

                GetLatestUpdateResponse latestUpdate = hubFlow.getLatestUpdate(monitorInstruction.getPatient().getId(), monitorInstruction.getTransactionType());

                String vaultTransactionVersion = parseVersion(latestUpdate, monitorInstruction.getTransactionType());
                log.info("Version returned by GetLatestUpdate for " + monitorInstruction.getPatient().inReadableFormat() + " and transactiontype " + monitorInstruction.getTransactionType().getTransactionTypeValueForGetLatestUpdate() + " is: " + vaultTransactionVersion + ". Last known local version is:" + lastKnownVersion);
                if (!StringUtils.equalsIgnoreCase(vaultTransactionVersion, lastKnownVersion)) {
                    return vaultTransactionVersion;
                }
//
//            } catch (DecryptorSoapException e) {
//                log.error("Got a soap exception from the decryptor. Possibly the session is not valid anymore. Will try to authenticate again for " + instruction.getPatient().inReadableFormat());
//                authenticate(instruction.getActorID());
            } catch (Exception e) {
                if (e instanceof Curable) {
                    log.error("Unexpected error while waiting for new safe contents for " + monitorInstruction.getPatient().inReadableFormat() + ". Will retry. Error details: ", e);
                } else {
                    throw e;
                }
            } catch (Throwable t) {
                throw t;
            }


            try {
                Thread.sleep(timeoutBetweenSafeChecksInMillis);
            } catch (InterruptedException e) {
                log.error("Failed to sleep", e);
            }

        } while (true);


    }


    public long getTimeoutBetweenSafeChecksInMillis() {
        return timeoutBetweenSafeChecksInMillis;
    }

    public void setTimeoutBetweenSafeChecksInMillis(long timeoutBetweenSafeChecksInMillis) {
        Monitor.timeoutBetweenSafeChecksInMillis = timeoutBetweenSafeChecksInMillis;
    }

    public static void main(String[] args) throws ConnectorException, GatewaySpecificErrorException, VitalinkException {

        Monitor monitor = new Monitor();
        String MSversion = monitor.waitForNewSafeContent(new MonitorInstruction(TransactionType.MEDICATION_SCHEME, PatientReader.loadPatientByKey(PatientKey.PATIENT_EXAMPLE.getValue()), AuthenticationConfigReader.GP_EXAMPLE), "270");
        String sumehrVersion = monitor.waitForNewSafeContent(new MonitorInstruction(TransactionType.SUMEHR, PatientReader.loadPatientByKey(PatientKey.PATIENT_EXAMPLE.getValue()), AuthenticationConfigReader.GP_EXAMPLE), "270");
        log.info("MS version: " + MSversion);
        log.info("Sumehr version: " + sumehrVersion);

    }


}