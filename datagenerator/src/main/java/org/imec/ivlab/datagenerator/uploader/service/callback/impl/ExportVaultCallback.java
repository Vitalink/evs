package org.imec.ivlab.datagenerator.uploader.service.callback.impl;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionSetResponse;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.config.EVSConfig;
import org.imec.ivlab.core.config.EVSProperties;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.hub.Hub;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.FileUtil;
import org.imec.ivlab.core.util.FilenameUtil;
import org.imec.ivlab.core.version.LocalVersionReader;
import org.imec.ivlab.datagenerator.SchemeExporter;
import org.imec.ivlab.datagenerator.exporter.export.AbstractTransactionsExporter;
import org.imec.ivlab.datagenerator.exporter.export.ExportResult;
import org.imec.ivlab.datagenerator.exporter.export.impl.DiaryNoteExporter;
import org.imec.ivlab.datagenerator.exporter.export.impl.MedicationExporter;
import org.imec.ivlab.datagenerator.exporter.export.impl.SumehrExporter;
import org.imec.ivlab.datagenerator.exporter.export.impl.VaccinationExporter;
import org.imec.ivlab.datagenerator.uploader.UploaderHelper;
import org.imec.ivlab.datagenerator.uploader.exception.CallbackException;
import org.imec.ivlab.datagenerator.uploader.model.Action;
import org.imec.ivlab.datagenerator.uploader.service.callback.Callback;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.Incurable;
import org.imec.ivlab.validator.report.FileWithValidatedKmehrsFormatter;
import org.imec.ivlab.validator.scanner.model.FileWithKmehrs;
import org.imec.ivlab.validator.scanner.model.FileWithValidatedKmehrs;
import org.imec.ivlab.validator.scanner.model.ValidatedKmehr;
import org.imec.ivlab.validator.validators.KmehrValidator;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.imec.ivlab.core.constants.CoreConstants.EXPORT_NAME_VITALINK_PDF;

public class ExportVaultCallback implements Callback {

    private final static Logger LOG = Logger.getLogger(ExportVaultCallback.class);

    private static final int MAX_NUMBER_OF_EXPORT_ATTEMPTS = 10;

    private File rootFolder;
    private File uploadFile;

    private TransactionType transactionType;
    private Action action;
    private Patient patient;
    private String actorID;
    private boolean validate;
    private boolean createGlobalSchemeAfterUpload;
    private boolean generateDailyMedicationScheme;
    private boolean generateSumehrOverview;
    private boolean generateDiaryNoteVisualization;
    private boolean generateVaccinationVisualization;
    private LocalDate dailyMedicationSchemeDate;
    private boolean generateGatewayMedicationScheme;

    public ExportVaultCallback(File rootFolder, File uploadFile, TransactionType transactionType, Action action, Patient patient, String actorID, boolean validate, boolean createGlobalSchemeAfterUpload, boolean generateDailyMedicationScheme,
        boolean generateSumehrOverview, LocalDate dailyMedicationSchemeDate, boolean generateGatewayMedicationScheme, boolean generateDiaryNoteVisualization, boolean generateVaccinationVisualization) {
        this.rootFolder = rootFolder;
        this.uploadFile = uploadFile;
        this.transactionType = transactionType;
        this.action = action;
        this.patient = patient;
        this.actorID = actorID;
        this.validate = validate;
        this.createGlobalSchemeAfterUpload = createGlobalSchemeAfterUpload;
        this.generateDailyMedicationScheme = generateDailyMedicationScheme;
        this.generateSumehrOverview = generateSumehrOverview;
        this.dailyMedicationSchemeDate = dailyMedicationSchemeDate;
        this.generateGatewayMedicationScheme = generateGatewayMedicationScheme;
        this.generateDiaryNoteVisualization = generateDiaryNoteVisualization;
        this.generateVaccinationVisualization = generateVaccinationVisualization;
    }

    @Override
    public void pass() throws CallbackException {

        File processedFile = UploaderHelper.getProcessedLocationForInstruction(rootFolder, uploadFile, patient.getKey(), actorID, action, transactionType);

        File exportFile = FileUtil.getFileWithNewExtension(new File(processedFile.getAbsolutePath()), "exp");

            try {

                if (TransactionType.MEDICATION_SCHEME.equals(transactionType)) {

                    ExportResult<GetTransactionSetResponse> exportResult = exportMedicationScheme(exportFile);
                    Kmehrmessage kmehrmessage = exportResult.getResponse() != null ? exportResult.getResponse().getKmehrmessage() : null;

                    String sizeString = "_empty";

                    if (kmehrmessage != null) {
                        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);
                        List<be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType> transactionsMSE = FolderUtil.getTransactions(folderType, CDTRANSACTIONvalues.MEDICATIONSCHEMEELEMENT);

                        if (CollectionsUtil.size(transactionsMSE) > 0) {
                            sizeString = "_size-" +  CollectionsUtil.size(transactionsMSE);
                        }
                    }

                    File exportFileWithAttributesInName = FilenameUtil.chompIfTooLong(new File(FileUtil.appendTextToFilename(exportFile, sizeString).getAbsolutePath()));

                    if (generateGatewayMedicationScheme && Hub.VITALINK.name().equals(EVSConfig.getInstance().getPropertyOrNull(EVSProperties.CHOSEN_HUB))) {
                        LOG.info("Starting gateway medication scheme generation");
                        exportMedicationSchemePdf(exportFileWithAttributesInName);
                        LOG.info("Completed Gateway medication scheme generation");
                    }

                    try {
                        FileUtils.moveFile(exportFile, exportFileWithAttributesInName);
                    } catch (FileExistsException e) {
                        LOG.warn("File exists already at path: " + exportFile.getAbsolutePath());
                    }

                    if (validate) {
                        validate(exportFileWithAttributesInName, kmehrmessage);
                    }

                    if (createGlobalSchemeAfterUpload) {
                        SchemeExporter.generateMSGlobalScheme(exportFileWithAttributesInName, kmehrmessage, patient);
                    }

                    if (generateDailyMedicationScheme) {
                        SchemeExporter.generateMSDailyScheme(exportFileWithAttributesInName, kmehrmessage, dailyMedicationSchemeDate, patient);
                    }

                }

                if (TransactionType.SUMEHR.equals(transactionType)) {

                    List<ExportResult<GetTransactionResponse>> exportResults = exportTransaction(exportFile, new SumehrExporter());

                    if (generateSumehrOverview && CollectionsUtil.notEmptyOrNull(exportResults)) {
                        for (ExportResult<GetTransactionResponse> result : exportResults) {
                            Kmehrmessage kmehrmessage = result.getResponse() != null ? result.getResponse().getKmehrmessage() : null;
                            if (kmehrmessage != null) {
                                SchemeExporter.generateSumehrOverview(result.getFile(), kmehrmessage);
                            }
                        }

                    }
                }

                if (TransactionType.DIARY_NOTE.equals(transactionType)) {

                    List<ExportResult<GetTransactionResponse>> exportResults = exportTransaction(exportFile, new DiaryNoteExporter());

                    if (generateDiaryNoteVisualization && CollectionsUtil.notEmptyOrNull(exportResults)) {
                        for (ExportResult<GetTransactionResponse> result : exportResults) {
                            Kmehrmessage kmehrmessage = result.getResponse() != null ? result.getResponse().getKmehrmessage() : null;
                            if (kmehrmessage != null) {
                                SchemeExporter.generateDiaryNoteVisualization(result.getFile(), kmehrmessage);
                            }
                        }

                    }
                }

                if (TransactionType.VACCINATION.equals(transactionType)) {

                    List<ExportResult<GetTransactionResponse>> exportResults = exportTransaction(exportFile, new VaccinationExporter());

                    if (generateVaccinationVisualization && CollectionsUtil.notEmptyOrNull(exportResults)) {
                        for (ExportResult<GetTransactionResponse> result : exportResults) {
                            Kmehrmessage kmehrmessage = result.getResponse() != null ? result.getResponse().getKmehrmessage() : null;
                            if (kmehrmessage != null) {
                                SchemeExporter.generateVaccinationVisualization(result.getFile(), kmehrmessage);
                            }
                        }

                        List<Kmehrmessage> kmehrmessages = exportResults
                            .stream()
                            .map(ExportResult::getResponse)
                            .map(GetTransactionResponse::getKmehrmessage)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                        SchemeExporter.generateVaccinationListVisualization(patient, exportFile.getParentFile(), kmehrmessages);

                    }
                }

            } catch (Exception e) {
                throw new CallbackException(e);
            }


    }


    @Override
    public void fail(String message) {

        //nothing to export in case of upload failure

    }

    private ExportResult<GetTransactionSetResponse> exportMedicationScheme(File exportFile) throws TechnicalConnectorException, VitalinkException, GatewaySpecificErrorException {

        MedicationExporter exporter = new MedicationExporter();
        int attemptCount = 1;

        while (attemptCount <= MAX_NUMBER_OF_EXPORT_ATTEMPTS) {
            try {
                ExportResult<GetTransactionSetResponse> exportResult = exporter.exportTransactionSet(transactionType, patient, actorID, exportFile.getParentFile(), FilenameUtils.getBaseName(exportFile.getAbsolutePath()), FilenameUtils.getExtension(exportFile.getAbsolutePath()));
                return exportResult;
            } catch (Exception e) {
                if (e instanceof Incurable) {
                    LOG.error(e);
                    writeErrorFile(e);
                    throw e;
                } else {
                    LOG.error("Export attempt: " + attemptCount + ". Got following exception: " + ExceptionUtils.getRootCauseMessage(e));
                }
                attemptCount++;
                if (attemptCount > MAX_NUMBER_OF_EXPORT_ATTEMPTS) {
                    throw e;
                }
            }
        }

        RuntimeException exception = new RuntimeException("Maximum number of export attempts reached: " + MAX_NUMBER_OF_EXPORT_ATTEMPTS);
        writeErrorFile(exception);
        throw exception;

    }

    private ExportResult<GetTransactionSetResponse> exportMedicationSchemePdf(File exportFile) throws TechnicalConnectorException, VitalinkException, GatewaySpecificErrorException {

        MedicationExporter exporter = new MedicationExporter();
        int attemptCount = 1;

        while (attemptCount <= MAX_NUMBER_OF_EXPORT_ATTEMPTS) {
            try {
                ExportResult<GetTransactionSetResponse> exportResult = exporter.exportTransactionSetPdf(transactionType, patient, actorID, exportFile.getParentFile(), FilenameUtils.getBaseName(FileUtil.appendTextToFilename(exportFile, "_" + EXPORT_NAME_VITALINK_PDF).getName()), null);
                return exportResult;
            } catch (Exception e) {
                if (e instanceof Incurable) {
                    LOG.error(e);
                    writeErrorFile(e);
                    throw e;
                } else {
                    LOG.error("Export attempt: " + attemptCount + ". Got following exception: " + ExceptionUtils.getRootCauseMessage(e));
                }
                attemptCount++;
                if (attemptCount > MAX_NUMBER_OF_EXPORT_ATTEMPTS) {
                    throw e;
                }
            }
        }

        RuntimeException exception = new RuntimeException("Maximum number of export attempts reached: " + MAX_NUMBER_OF_EXPORT_ATTEMPTS);
        writeErrorFile(exception);
        throw exception;

    }

    private void writeErrorFile(Exception e) {

        File processedFile = UploaderHelper.getProcessedLocationForInstruction(rootFolder, uploadFile, patient.getKey(), actorID, action, transactionType);
        File errorMessageFile = FileUtil.getFileWithNewExtension(processedFile, "err");

        try {
            LOG.debug("Will write file with error information to: " + errorMessageFile.getAbsolutePath());
            FileUtils.writeStringToFile(errorMessageFile, "Generated by " + LocalVersionReader.getInstalledSoftwareAndVersion() + System.lineSeparator() + System.lineSeparator() + ExceptionUtils.getStackTrace(e), "UTF-8");
        } catch (IOException e1) {
            throw new RuntimeException("Error when writing error information file: " + errorMessageFile.getAbsolutePath(), e1);
        }
    }

    private List<ExportResult<GetTransactionResponse>> exportTransaction(File exportFile, AbstractTransactionsExporter exporter) throws TechnicalConnectorException, VitalinkException, GatewaySpecificErrorException {

        int attemptCount = 1;

        while (attemptCount <= MAX_NUMBER_OF_EXPORT_ATTEMPTS) {
            try {
                List<ExportResult<GetTransactionResponse>> exportResults = exporter.exportTransactions(transactionType, patient, actorID, exportFile.getParentFile(), FilenameUtils.getBaseName(exportFile.getAbsolutePath()), FilenameUtils.getExtension(exportFile.getAbsolutePath()));
                return exportResults;
            } catch (Exception e) {
                if (e instanceof Incurable) {
                    LOG.error(e);
                    writeErrorFile(e);
                    throw e;

                } else {
                    LOG.error("Export attempt: " + attemptCount + ". Got following exception: " + ExceptionUtils.getRootCauseMessage(e));
                }
                attemptCount++;
                if (attemptCount > MAX_NUMBER_OF_EXPORT_ATTEMPTS) {
                    throw e;
                }
            }
        }

        RuntimeException exception = new RuntimeException("Maximum number of export attempts reached: " + MAX_NUMBER_OF_EXPORT_ATTEMPTS);
        writeErrorFile(exception);
        throw exception;

    }

    private void validate(File inputFile, Kmehrmessage kmehrmessage) {

        try {

            LOG.info("Starting validation");

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
            outputFile = FilenameUtil.chompIfTooLong(outputFile);
            try {
                FileUtils.writeStringToFile(outputFile, report, "UTF-8");
            } catch (IOException e) {
                LOG.error("Failed to write the validation report.", e);
            }

        } catch (Throwable t) {

            LOG.error("Error while validating export", t);

        }

    }



}
