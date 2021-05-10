package org.imec.ivlab.datagenerator.uploader;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.imec.ivlab.core.jcommander.HubConverter;
import org.imec.ivlab.core.jcommander.LocalDateConverter;
import org.imec.ivlab.core.jcommander.PathToFileConverter;
import org.imec.ivlab.core.jcommander.SearchTypeConverter;
import org.imec.ivlab.core.model.hub.Hub;
import org.imec.ivlab.core.model.hub.SearchType;
import org.imec.ivlab.datagenerator.uploader.dateshift.ShiftAction;
import org.imec.ivlab.datagenerator.uploader.dateshift.ShiftActionConverter;

import java.io.File;
import java.time.LocalDate;

@Parameters(separators = "=")
public class UploaderArguments {

    @Parameter(names = "-rootDir", description = "The root directory from which to start uploading", required = true, converter = PathToFileConverter.class)
    private File rootDir;

    @Parameter(names = "-exportAfterUpload", required = true, description = "Perform an export after the upload", arity = 1)
    private boolean exportAfterUpload;

    @Parameter(names = "-validateExportAfterUpload", required = true, description = "Break the glass if the Therapeutic Relation is missing", arity = 1)
    private boolean validateExportAfterUpload;

    @Parameter(names = "-generateGlobalMedicationScheme", description = "Generate a global medication schema", arity = 1)
    private boolean generateGlobalMedicationScheme;

    @Parameter(names = "-generateDailyMedicationScheme", description = "Generate a daily medication schema", arity = 1)
    private boolean generateDailyMedicationScheme;

    @Parameter(names = "-dailyMedicationSchemeDate", description = "The date to use for the daily medication scheme. If no date is supplied, the date of today is used", converter = LocalDateConverter.class)
    private LocalDate dailyMedicationSchemeDate = LocalDate.now();

    @Parameter(names = "-writeAsIs", required = true, description = "Write the content as is without ANY modification", arity = 1)
    private boolean writeAsIs;

    @Parameter(names = "-startTransactonId", description = "The transactionId to start the sequence with")
    private String startTransactionId;

    @Parameter(names = "-shiftAction", description = "The shiftaction to apply", converter = ShiftActionConverter.class)
    private ShiftAction shiftAction = ShiftAction.NO_TAG_AND_NO_SHIFT;

    @Parameter(names = "-generateSumehrOverview", description = "Generate a an overview for every sumehr transaction", arity = 1)
    private boolean generateSumehrOverview;

    @Parameter(names = "-generateDiaryNoteVisualization", description = "Generate a visualization for every diarynote transaction", arity = 1)
    private boolean generateDiaryNoteVisualization;

    @Parameter(names = "-generateVaccinationVisualization", description = "Generate a visualization for every vaccination transaction", arity = 1)
    private boolean generateVaccinationVisualization;

    @Parameter(names = "-generateGatewayMedicationScheme", description = "Generate a gateway medication scheme", required = true, arity = 1)
    private boolean generateGatewayMedicationScheme;

    @Parameter(names = "-hub", description = "The hub to connect to", converter = HubConverter.class)
    private Hub hub = Hub.VITALINK;

    @Parameter(names = "-searchType", description = "The searchtype to use when searching", converter = SearchTypeConverter.class)
    private SearchType searchType = SearchType.LOCAL;

    @Parameter(names = "-autoGenerateMSTransactionAuthor", description = "Overwrite the author of the MS transaction based on actor config", arity = 1)
    private boolean autoGenerateMSTransactionAuthor;

    @Parameter(names = "-filterOutTransactionsHavingPatientAccessNo", description = "Do not attempt to get hub transactions that are marked as PatientAccess=no", arity = 1)
    private boolean filterOutTransactionsHavingPatientAccessNo;

    public File getRootDir() {
        return rootDir;
    }

    public void setRootDir(File rootDir) {
        this.rootDir = rootDir;
    }

    public boolean isExportAfterUpload() {
        return exportAfterUpload;
    }

    public void setExportAfterUpload(boolean exportAfterUpload) {
        this.exportAfterUpload = exportAfterUpload;
    }

    public boolean isWriteAsIs() {
        return writeAsIs;
    }

    public void setWriteAsIs(boolean writeAsIs) {
        this.writeAsIs = writeAsIs;
    }

    public boolean isValidateExportAfterUpload() {
        return validateExportAfterUpload;
    }

    public void setValidateExportAfterUpload(boolean validateExportAfterUpload) {
        this.validateExportAfterUpload = validateExportAfterUpload;
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

    public String getStartTransactionId() {
        return startTransactionId;
    }

    public void setStartTransactionId(String startTransactionId) {
        this.startTransactionId = startTransactionId;
    }

    public ShiftAction getShiftAction() {
        return shiftAction;
    }

    public void setShiftAction(ShiftAction shiftAction) {
        this.shiftAction = shiftAction;
    }

    public boolean isGenerateGatewayMedicationScheme() {
        return generateGatewayMedicationScheme;
    }

    public void setGenerateGatewayMedicationScheme(boolean generateGatewayMedicationScheme) {
        this.generateGatewayMedicationScheme = generateGatewayMedicationScheme;
    }

    public Hub getHub() {
        return hub;
    }

    public void setHub(Hub hub) {
        this.hub = hub;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    @Override
    public String toString() {
        return "UploaderArguments{" +
            "rootDir=" + rootDir +
            ", exportAfterUpload=" + exportAfterUpload +
            ", validateExportAfterUpload=" + validateExportAfterUpload +
            ", generateGlobalMedicationScheme=" + generateGlobalMedicationScheme +
            ", generateDailyMedicationScheme=" + generateDailyMedicationScheme +
            ", dailyMedicationSchemeDate=" + dailyMedicationSchemeDate +
            ", writeAsIs=" + writeAsIs +
            ", startTransactionId='" + startTransactionId + '\'' +
            ", shiftAction=" + shiftAction +
            ", generateSumehrOverview=" + generateSumehrOverview +
            ", generateDiaryNoteVisualization=" + generateDiaryNoteVisualization +
            ", generateVaccinationVisualization=" + generateVaccinationVisualization +
            ", generateGatewayMedicationScheme=" + generateGatewayMedicationScheme +
            ", hub=" + hub +
            ", searchType=" + searchType +
            ", autoGenerateMSTransactionAuthor=" + autoGenerateMSTransactionAuthor +
            ", filterOutTransactionsHavingPatientAccessNo=" + filterOutTransactionsHavingPatientAccessNo +
            '}';
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

    public boolean isAutoGenerateMSTransactionAuthor() {
        return autoGenerateMSTransactionAuthor;
    }

    public void setAutoGenerateMSTransactionAuthor(boolean autoGenerateMSTransactionAuthor) {
        this.autoGenerateMSTransactionAuthor = autoGenerateMSTransactionAuthor;
    }

    public boolean isFilterOutTransactionsHavingPatientAccessNo() {
        return filterOutTransactionsHavingPatientAccessNo;
    }

    public void setFilterOutTransactionsHavingPatientAccessNo(boolean filterOutTransactionsHavingPatientAccessNo) {
        this.filterOutTransactionsHavingPatientAccessNo = filterOutTransactionsHavingPatientAccessNo;
    }
}
