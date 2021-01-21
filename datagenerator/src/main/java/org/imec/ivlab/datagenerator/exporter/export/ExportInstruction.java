package org.imec.ivlab.datagenerator.exporter.export;


import org.imec.ivlab.core.model.upload.TransactionType;

import java.io.File;
import java.time.LocalDate;

public class ExportInstruction {

    private TransactionType transactionType;
    private File exportDir;
    private boolean validate;
    private boolean generateGlobalMedicationScheme;
    private boolean generateDailyMedicationScheme;
    private boolean generateGatewayMedicationScheme;
    private boolean generateSumehrOverview;
    private boolean generateDiaryNoteVisualization;
    private boolean generateVaccinationVisualization;
    private LocalDate dailyMedicationSchemeDate;
    private String actorKey;


    public File getExportDir() {
        return exportDir;
    }

    public void setExportDir(File exportDir) {
        this.exportDir = exportDir;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public boolean isGenerateGlobalMedicationScheme() {
        return generateGlobalMedicationScheme;
    }

    public void setGenerateGlobalMedicationScheme(boolean generateGlobalMedicationScheme) {
        this.generateGlobalMedicationScheme = generateGlobalMedicationScheme;
    }

    public boolean isGenerateDailyMedicationScheme() {
        return generateDailyMedicationScheme;
    }

    public void setGenerateDailyMedicationScheme(boolean generateDailyMedicationScheme) {
        this.generateDailyMedicationScheme = generateDailyMedicationScheme;
    }

    public LocalDate getDailyMedicationSchemeDate() {
        return dailyMedicationSchemeDate;
    }

    public void setDailyMedicationSchemeDate(LocalDate dailyMedicationSchemeDate) {
        this.dailyMedicationSchemeDate = dailyMedicationSchemeDate;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getActorKey() {
        return actorKey;
    }

    public void setActorKey(String actorKey) {
        this.actorKey = actorKey;
    }

    public boolean isGenerateSumehrOverview() {
        return generateSumehrOverview;
    }

    public void setGenerateSumehrOverview(boolean generateSumehrOverview) {
        this.generateSumehrOverview = generateSumehrOverview;
    }

    public boolean isGenerateDiaryNoteVisualization() {
        return generateDiaryNoteVisualization;
    }

    public void setGenerateDiaryNoteVisualization(boolean generateDiaryNoteVisualization) {
        this.generateDiaryNoteVisualization = generateDiaryNoteVisualization;
    }

    public boolean isGenerateVaccinationVisualization() {
        return generateVaccinationVisualization;
    }

    public void setGenerateVaccinationVisualization(boolean generateVaccinationVisualization) {
        this.generateVaccinationVisualization = generateVaccinationVisualization;
    }

    public boolean isGenerateGatewayMedicationScheme() {
        return generateGatewayMedicationScheme;
    }

    public void setGenerateGatewayMedicationScheme(boolean generateGatewayMedicationScheme) {
        this.generateGatewayMedicationScheme = generateGatewayMedicationScheme;
    }
}