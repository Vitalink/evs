package org.imec.ivlab.datagenerator.exporter;

import com.beust.jcommander.JCommander;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.config.EVSConfig;
import org.imec.ivlab.core.config.EVSProperties;
import org.imec.ivlab.core.model.patient.PatientReader;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.version.LocalVersionReader;
import org.imec.ivlab.core.version.VersionManager;
import org.imec.ivlab.datagenerator.exporter.export.ExportInstruction;
import org.imec.ivlab.datagenerator.exporter.monitor.MonitorInstruction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VaultExporterRunner {

    private final static Logger log = Logger.getLogger(VaultExporterRunner.class);


    private static final long TIME_IN_MILLIS_BETWEEN_LAUNCHING_VAULT_EXPORTERS = 10000;
    private static final int MAX_NUMBER_OF_THREADS = 100;
    public static final String GET_LATEST_UPDATE_REQUEST_ACTION = "GetLatestUpdateRequest";
    public static final String GET_LATEST_UPDATE_RESPONSE_ACTION = "GetLatestUpdateResponse";
    public static final String GET_ETK_REQUEST_ACTION = "GetEtkRequest";
    public static final String GET_ETK_RESPONSE_ACTION = "GetEtkResponse";

    public static void main(String[] args) {

        log.info("Starting " + LocalVersionReader.getInstalledSoftwareAndVersion());
        VersionManager.printUpdateMessage();

        String[] commandlineArguments = args;
        final VaultExporterArguments vaultExporterArguments = new VaultExporterArguments();
        // parse the arguments
        final JCommander commander = new JCommander(vaultExporterArguments);
        commander.setAcceptUnknownOptions(false);
        commander.parse(commandlineArguments);

        VaultExporterRunner exporter = new VaultExporterRunner();
        exporter.start(vaultExporterArguments);

    }
    
    public void start(VaultExporterArguments arguments) {

        List<String> patientKeys = arguments.getPatientKeys();

        if (CollectionsUtil.emptyOrNull(patientKeys)) {
            throw new RuntimeException("No patients specified");
        }

        if (arguments.getExportDir() == null) {
            throw new RuntimeException("Export directory must be specified");
        }

        if (!(arguments.getExportDir().exists() && arguments.getExportDir().isDirectory())) {
            throw new RuntimeException("Export directory must be a directory and exist");
        }

        if (arguments.getTransactionType() == null) {
            throw new RuntimeException("No transaction type specified");
        }

        log.info("About to start exporting the vault for transactionType " + arguments.getTransactionType() + " and " + arguments.getPatientKeys().size() + " patients.");

//        CommunicationLoggerConfiguration.getInstance().addToIgnoreList(GET_LATEST_UPDATE_REQUEST_ACTION);
//        CommunicationLoggerConfiguration.getInstance().addToIgnoreList(GET_LATEST_UPDATE_RESPONSE_ACTION);
//        CommunicationLoggerConfiguration.getInstance().addToIgnoreList(GET_ETK_REQUEST_ACTION);
//        CommunicationLoggerConfiguration.getInstance().addToIgnoreList(GET_ETK_RESPONSE_ACTION);

        ExecutorService executor = Executors.newFixedThreadPool(MAX_NUMBER_OF_THREADS);

        log.info("Maximum number of simultaneous patient export threads: " + MAX_NUMBER_OF_THREADS);

        if (patientKeys.size() > MAX_NUMBER_OF_THREADS) {
            log.warn("Only " + MAX_NUMBER_OF_THREADS + " patient export threads will be started initially. The remaining patient exports will be triggered when threads become availble");
        }

        EVSConfig.getInstance().setProperty(EVSProperties.CHOSEN_HUB, arguments.getHub().name());
        EVSConfig.getInstance().setProperty(EVSProperties.SEARCH_TYPE, arguments.getSearchType().name());
        EVSConfig.getInstance().setProperty(EVSProperties.FILTER_OUT_TRANSACTIONS_HAVING_PATIENT_ACCESS_NO, String.valueOf(arguments.isFilterOutTransactionsHavingPatientAccessNo()));

        for (int patientKeyIndex = 0; patientKeyIndex < patientKeys.size(); patientKeyIndex++) {

            String patientKey = patientKeys.get(patientKeyIndex);

            Patient patient = PatientReader.loadPatientByKey(patientKey);
            log.info("Initializing exporter for patient " + (patientKeyIndex + 1) + "/" + patientKeys.size() + ": " + patient.inReadableFormat());

            MonitorInstruction monitorInstruction = new MonitorInstruction(arguments.getTransactionType(), patient, arguments.getActorKey());

            monitorInstruction.setActorID(arguments.getActorKey());
            monitorInstruction.setBreakTheGlassIfTRMissing(arguments.isBreakTheGlassIfTRMissing());

            ExportInstruction exportInstruction = new ExportInstruction();
            exportInstruction.setExportDir(arguments.getExportDir());
            exportInstruction.setDailyMedicationSchemeDate(arguments.getDailyMedicationSchemeDate());
            exportInstruction.setGenerateDailyMedicationScheme(arguments.isGenerateDailyMedicationScheme());
            exportInstruction.setGenerateGlobalMedicationScheme(arguments.isGenerateGlobalMedicationScheme());
            exportInstruction.setValidate(arguments.isValidate());
            exportInstruction.setActorKey(arguments.getActorKey());
            exportInstruction.setTransactionType(arguments.getTransactionType());
            exportInstruction.setGenerateSumehrOverview(arguments.isGenerateSumehrOverview());
            exportInstruction.setGenerateDiaryNoteVisualization(arguments.isGenerateDiaryNoteVisualization());
            exportInstruction.setGenerateGatewayMedicationScheme(arguments.isGenerateGatewayMedicationScheme());

            executor.execute(new VaultExporterRunnable(monitorInstruction, exportInstruction));

            // Delay after initializing the first patient
            if (patientKeyIndex < 1) {
                try {
                    Thread.sleep(TIME_IN_MILLIS_BETWEEN_LAUNCHING_VAULT_EXPORTERS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }


    }


}
