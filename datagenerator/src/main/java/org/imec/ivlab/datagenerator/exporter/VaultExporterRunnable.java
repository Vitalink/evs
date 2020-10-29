package org.imec.ivlab.datagenerator.exporter;

import be.fgov.ehealth.hubservices.core.v3.GetTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionSetResponse;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.config.EVSConfig;
import org.imec.ivlab.core.config.EVSProperties;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.hub.Hub;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.ConsoleUtils;
import org.imec.ivlab.core.util.DateUtils;
import org.imec.ivlab.core.util.FileUtil;
import org.imec.ivlab.core.util.FilenameUtil;
import org.imec.ivlab.core.version.LocalVersionReader;
import org.imec.ivlab.core.version.VersionCheckResult;
import org.imec.ivlab.core.version.VersionManager;
import org.imec.ivlab.datagenerator.SchemeExporter;
import org.imec.ivlab.datagenerator.exporter.export.ExportInstruction;
import org.imec.ivlab.datagenerator.exporter.export.ExportResult;
import org.imec.ivlab.datagenerator.exporter.export.impl.DiaryNoteExporter;
import org.imec.ivlab.datagenerator.exporter.export.impl.MedicationExporter;
import org.imec.ivlab.datagenerator.exporter.export.impl.SumehrExporter;
import org.imec.ivlab.datagenerator.exporter.monitor.Monitor;
import org.imec.ivlab.datagenerator.exporter.monitor.MonitorInstruction;
import org.imec.ivlab.datagenerator.util.kmehrmodifier.VersionWriter;
import org.imec.ivlab.ehconnector.hub.exception.incurable.Incurable;
import org.imec.ivlab.validator.report.FileWithValidatedKmehrsFormatter;
import org.imec.ivlab.validator.scanner.model.FileWithKmehrs;
import org.imec.ivlab.validator.scanner.model.FileWithValidatedKmehrs;
import org.imec.ivlab.validator.scanner.model.ValidatedKmehr;
import org.imec.ivlab.validator.validators.KmehrValidator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.imec.ivlab.core.constants.CoreConstants.EXPORT_NAME_VITALINK_PDF;

public class VaultExporterRunnable implements Runnable {

    private MonitorInstruction monitorInstruction;
    private ExportInstruction exportInstruction;

    private final static Logger LOG = Logger.getLogger(VaultExporterRunnable.class);

    private static final long TIMEOUT_BETWEEN_VAULT_CHECKS = 10000;
    private static final long TIMEOUT_AFTER_EXCEPTION_IN_MILLIS = 10000;

    public VaultExporterRunnable(MonitorInstruction monitorInstruction, ExportInstruction exportInstruction) {
        this.exportInstruction = exportInstruction;
        this.monitorInstruction = monitorInstruction;
    }



    @Override
    public void run() {

        Monitor monitor = new Monitor();
        monitor.setTimeoutBetweenSafeChecksInMillis(TIMEOUT_BETWEEN_VAULT_CHECKS);

        String localVersion = "";

        boolean incurableErrorOccurred = false;
        Throwable incurableError = null;
        boolean breakTheGlass = false;

        File outputDirectory = new File(exportInstruction.getExportDir().getAbsolutePath() + File.separator + monitorInstruction.getPatient().getKey());

        boolean versionInfoChecked = false;

        while (!incurableErrorOccurred) {

            if (!versionInfoChecked) {
                try {
                    VersionCheckResult versionCheckResult = VersionManager.getVersionCheckResult();
                    File versionOutputFile = new File(
                            outputDirectory + File.separator + DateUtils.formatDate(DateUtils.getDate(), DateUtils.DEFAULT_DATETIME_FORMAT_SHORT) + VersionWriter.VERSION_FILE_NAME_PART);
                    versionInfoChecked = VersionWriter.writeVersionInfoFileIfOutdated(versionOutputFile, versionCheckResult);
                } catch (Exception e) {
                    LOG.error(e);
                }
            }

            try {

                String vaultVersion = monitor.waitForNewSafeContent(monitorInstruction, localVersion);

                LOG.info(System.lineSeparator() + ConsoleUtils.emphasizeTitle("New version with number " + vaultVersion + " detected in vault for patient " + monitorInstruction.getPatient().inReadableFormat() +  " and type " + exportInstruction.getTransactionType()));

                localVersion = vaultVersion;

                if (TransactionType.MEDICATION_SCHEME.equals(monitorInstruction.getTransactionType())) {

                    MedicationExporter medicationExporter = new MedicationExporter();
                    ExportResult<GetTransactionSetResponse> exportResult = medicationExporter.exportTransactionSet(exportInstruction.getTransactionType(), monitorInstruction.getPatient(), exportInstruction.getActorKey(), outputDirectory, null, null);

                    if (exportInstruction.isValidate()) {
                        Kmehrmessage kmehrmessage = exportResult.getResponse() != null ? exportResult.getResponse().getKmehrmessage() : null;
                        validate(exportResult.getFile(), kmehrmessage);
                    }

                    Kmehrmessage kmehrmessage = exportResult.getResponse() != null ? exportResult.getResponse().getKmehrmessage() : null;
                    if (exportInstruction.isGenerateGlobalMedicationScheme()) {
                        SchemeExporter.generateMSGlobalScheme(exportResult.getFile(), kmehrmessage, monitorInstruction.getPatient());
                    }

                    if (exportInstruction.isGenerateDailyMedicationScheme()) {
                        SchemeExporter.generateMSDailyScheme(exportResult.getFile(), kmehrmessage, exportInstruction.getDailyMedicationSchemeDate(), monitorInstruction.getPatient());
                    }

                    if (exportInstruction.isGenerateGatewayMedicationScheme() && Hub.VITALINK.name().equals(EVSConfig.getInstance().getPropertyOrNull(EVSProperties.CHOSEN_HUB))) {
                        LOG.info("Starting gateway medication scheme generation");
                        medicationExporter.exportTransactionSetPdf(exportInstruction.getTransactionType(), monitorInstruction.getPatient(), exportInstruction.getActorKey(), outputDirectory, FilenameUtils.getBaseName(FileUtil.appendTextToFilename(exportResult.getFile(), "_" + EXPORT_NAME_VITALINK_PDF).getName()), null);
                        LOG.info("Completed Gateway medication scheme generation");
                    }

                }

                if (TransactionType.VACCINATIONS.equals(monitorInstruction.getTransactionType())) {

                    MedicationExporter medicationExporter = new MedicationExporter();
                    medicationExporter.exportTransactionSet(exportInstruction.getTransactionType(), monitorInstruction.getPatient(), exportInstruction.getActorKey(), outputDirectory, null, null);
                }

                if (TransactionType.SUMEHR.equals(monitorInstruction.getTransactionType())) {

                    SumehrExporter sumehrExporter = new SumehrExporter();
                    List<ExportResult<GetTransactionResponse>> results = sumehrExporter.exportTransactions(exportInstruction.getTransactionType(), monitorInstruction.getPatient(), exportInstruction.getActorKey(), outputDirectory, null, null);

                    if (exportInstruction.isGenerateSumehrOverview() && CollectionsUtil.notEmptyOrNull(results)) {
                        for (ExportResult<GetTransactionResponse> result : results) {
                            Kmehrmessage kmehrmessage = result.getResponse() != null ? result.getResponse().getKmehrmessage() : null;
                            if (kmehrmessage != null) {
                                SchemeExporter.generateSumehrOverview(result.getFile(), kmehrmessage);
                            }
                        }

                    }

                }


                if (TransactionType.DIARY_NOTE.equals(monitorInstruction.getTransactionType())) {

                    DiaryNoteExporter diaryNoteExporter = new DiaryNoteExporter();
                    List<ExportResult<GetTransactionResponse>> results = diaryNoteExporter.exportTransactions(exportInstruction.getTransactionType(), monitorInstruction.getPatient(), exportInstruction.getActorKey(), outputDirectory, null, null);

                    if (exportInstruction.isGenerateSumehrOverview() && CollectionsUtil.notEmptyOrNull(results)) {
                        for (ExportResult<GetTransactionResponse> result : results) {
                            Kmehrmessage kmehrmessage = result.getResponse() != null ? result.getResponse().getKmehrmessage() : null;
                            if (kmehrmessage != null) {
                                SchemeExporter.generateDiaryNoteVisualization(result.getFile(), kmehrmessage);
                            }
                        }

                    }
                }


                LOG.info(System.lineSeparator() + ConsoleUtils.emphasizeTitle("Vault export completed successfully for patient " + monitorInstruction.getPatient().inReadableFormat() +  " and type " + exportInstruction.getTransactionType()));

            } catch (Exception e) {
                if (e instanceof Incurable) {
                    LOG.error(e);
                    incurableErrorOccurred = true;
                    incurableError = e;
                } else {
                    LOG.error("Unexpected error while exporting vault for patient " + monitorInstruction.getPatient().inReadableFormat() + ". Will retry. Error details: ", e);
                    LOG.info(System.lineSeparator() + ConsoleUtils.emphasizeTitle("Vault export completed with errors for patient " + monitorInstruction.getPatient().inReadableFormat() +  " and type " + exportInstruction.getTransactionType()));
                }
            } catch (Throwable e) {
                LOG.error("Unexpected error while exporting vault for patient " + monitorInstruction.getPatient().inReadableFormat() + ". Will retry. Error details: ", e);
                LOG.info(System.lineSeparator() + ConsoleUtils.emphasizeTitle("Vault export completed with errors for patient " + monitorInstruction.getPatient().inReadableFormat() +  " and type " + exportInstruction.getTransactionType()));
            }

            VersionManager.printUpdateMessage();

            try {
                Thread.sleep(TIMEOUT_AFTER_EXCEPTION_IN_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        logUnrecoverableError(exportInstruction.getExportDir(), monitorInstruction.getPatient().getKey(), incurableError);

    }

    private void logUnrecoverableError(File exportDir, String patientKey, Throwable t) {

        String errorMessage = ExceptionUtils.getStackTrace(t);

        String filename = DateUtils.formatDateTime(DateUtils.getDate(), DateUtils.DEFAULT_DATETIME_FORMAT_SHORT) + "_" + getErrorOneliner(t);

        File errorMessageFile = new File(exportDir.getAbsolutePath() + File.separator + patientKey + File.separator + filename + ".err");

        errorMessageFile = FilenameUtil.chompIfTooLong(errorMessageFile);

        FileUtil.createDirectoriesRecursively(exportDir.getAbsolutePath());

        try {
            LOG.debug("Will write file with error information to: " + errorMessageFile.getAbsolutePath());
            FileUtils.writeStringToFile(errorMessageFile, "Generated by " + LocalVersionReader.getInstalledSoftwareAndVersion() + System.lineSeparator() + System.lineSeparator() + errorMessage, "UTF-8");
        } catch (IOException e) {
            LOG.error("Error when writing error information file: " + errorMessageFile.getAbsolutePath(), e);
        }

    }

    private String getErrorOneliner(Throwable t) {
        String errorMessage = ExceptionUtils.getRootCauseMessage(t);
        return errorMessage.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }

    private void validate(File inputFile, Kmehrmessage kmehrmessage) {

        try {

            LOG.info("Starting validation");

            KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(inputFile);

            if (kmehrEntryList == null || CollectionsUtil.emptyOrNull(kmehrEntryList.getBusinessDataList())) {
                LOG.info("No kmehr entries, there's nothing to validate in file: " + inputFile.getAbsolutePath());
            }

            KmehrValidator validator = new KmehrValidator();
            FileWithKmehrs fileWithKmehrs = new FileWithKmehrs(inputFile, kmehrmessage);
            ArrayList<FileWithKmehrs> filesWithKmehrs = new ArrayList<>();
            filesWithKmehrs.add(fileWithKmehrs);
            List<FileWithValidatedKmehrs> filesWithValidatedKmehrs = validator.performValidation(filesWithKmehrs);

            String metrics = "OK";
            if (filesWithValidatedKmehrs.get(0) != null && CollectionsUtil.notEmptyOrNull(filesWithValidatedKmehrs.get(0).getValidatedKmehrs())
                    && filesWithValidatedKmehrs.get(0).getValidatedKmehrs().get(0).getValidationResult() != null   ) {
                ValidatedKmehr validatedKmehr = filesWithValidatedKmehrs.get(0).getValidatedKmehrs().get(0);
                if (validatedKmehr.getValidationResult().getFailedRulesCount() == 0 && validatedKmehr.getValidationResult().getInterruptedRulesCount() ==  0) {
                    metrics = "OK";
                } else {
                    metrics = (validatedKmehr.getValidationResult().getFailedRulesCount() + validatedKmehr.getValidationResult().getInterruptedRulesCount()) + "-FAILED";
                }
            }

            String report = FileWithValidatedKmehrsFormatter.getOutput(filesWithValidatedKmehrs);

            File outputFile = new File(inputFile.getAbsolutePath());
            outputFile = FileUtil.appendTextToFilename(outputFile, "_VALIDATION-" + metrics);
            outputFile = FileUtil.getFileWithNewExtension(outputFile, "val");
            try {
                FileUtils.writeStringToFile(outputFile, report, "UTF-8");
            } catch (IOException e) {
                LOG.error("Failed to write the validation report.", e);
            }

            LOG.info("Validation completed");

        } catch (Throwable t) {

            LOG.error("Error while validating export", t);

        }



    }


}
