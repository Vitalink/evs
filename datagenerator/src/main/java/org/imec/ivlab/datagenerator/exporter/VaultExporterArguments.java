package org.imec.ivlab.datagenerator.exporter;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.jcommander.HubConverter;
import org.imec.ivlab.core.jcommander.LogCommunicationTypeConverter;
import org.imec.ivlab.core.jcommander.SearchTypeConverter;
import org.imec.ivlab.core.model.hub.LogCommunicationType;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;
import org.imec.ivlab.core.jcommander.LocalDateConverter;
import org.imec.ivlab.core.jcommander.PathToFileConverter;
import org.imec.ivlab.core.jcommander.StringListConverter;
import org.imec.ivlab.core.jcommander.TransactionTypeFromGetTransactionListValueConverter;
import org.imec.ivlab.core.model.hub.Hub;
import org.imec.ivlab.core.model.hub.SearchType;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@Parameters(separators = "=")
@Getter
@Setter
public class VaultExporterArguments {

    @Parameter(names = "-exportDir", description = "The root directory to which to export", required = true, converter = PathToFileConverter.class)
    private File exportDir;

    @Parameter(names = "-patients", description = "Comma separated list of patients to export the vault for", required = true, converter = StringListConverter.class)
    private List<String> patientKeys;

    @Parameter(names = "-transactionType", description = "The transaction type of the transactions to export the vault for: medicationscheme or sumehr", required = true, converter = TransactionTypeFromGetTransactionListValueConverter.class)
    private TransactionType transactionType;

    @Parameter(names = "-breakTheGlassIfTRMissing", description = "Break the glass if the Therapeutic Relation is missing", required = false, arity = 1)
    private boolean breakTheGlassIfTRMissing;

    @Parameter(names = "-validate", description = "Validate the vault content", required = true, arity = 1)
    private boolean validate;

    @Parameter(names = "-generateGlobalMedicationScheme", description = "Generate a global medication schema after export", required = true, arity = 1)
    private boolean generateGlobalMedicationScheme;

    @Parameter(names = "-generateDailyMedicationScheme", description = "Generate a daily medication schema after export", required = true, arity = 1)
    private boolean generateDailyMedicationScheme;

    @Parameter(names = "-dailyMedicationSchemeDate", description = "The date to use for the daily medication scheme. If no date is supplied, the date of today is used", converter = LocalDateConverter.class)
    private LocalDate dailyMedicationSchemeDate = LocalDate.now();

    @Parameter(names = "-actor", description = "The name of the actor to connect to ehealth with", required = true)
    private String actorKey = AuthenticationConfigReader.DEFAULT_CONFIGURATION;

    @Parameter(names = "-generateSumehrOverview", description = "Generate an overview for every sumehr transaction", arity = 1)
    private boolean generateSumehrOverview;

    @Parameter(names = "-generateDiaryNoteVisualization", description = "Generate a visualization for every diarynote transaction", arity = 1)
    private boolean generateDiaryNoteVisualization;

    @Parameter(names = "-generateVaccinationVisualization", description = "Generate a visualization for every vaccination transaction", arity = 1)
    private boolean generateVaccinationVisualization;

    @Parameter(names = "-generateChildPreventionVisualization", description = "Generate a visualization for every childprevention transaction", arity = 1)
    private boolean generateChildPreventionVisualization;

    @Parameter(names = "-generatePopulationBasedScreeningVisualization", description = "Generate a visualization for every populationbasedscreening transaction", arity = 1)
    private boolean generatePopulationBasedScreeningVisualization;

    @Parameter(names = "-generateGatewayMedicationScheme", description = "Generate a gateway medication scheme after export", required = true, arity = 1)
    private boolean generateGatewayMedicationScheme;

    @Parameter(names = "-hub", description = "The hub to connect to", converter = HubConverter.class)
    private Hub hub = Hub.VITALINK;

    @Parameter(names = "-searchType", description = "The searchtype to use when searching", converter = SearchTypeConverter.class)
    private SearchType searchType = SearchType.LOCAL;

    @Parameter(names = "-filterOutTransactionsHavingPatientAccessNo", description = "Do not attempt to get hub transactions that are marked as PatientAccess=no", arity = 1)
    private boolean filterOutTransactionsHavingPatientAccessNo;

    @Parameter(names = "-logCommunicationType", description = "What kind communication to log", converter = LogCommunicationTypeConverter.class)
    private LogCommunicationType logCommunicationType = LogCommunicationType.WITHOUT_SECURITY;

}
