package org.imec.ivlab.datagenerator.exporter.export;


import java.io.File;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.model.upload.TransactionType;

@Getter
@Setter
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
    private boolean generateChildPreventionVisualization;
    private boolean generatePopulationBasedScreening;
    private LocalDate dailyMedicationSchemeDate;
    private String actorKey;

}