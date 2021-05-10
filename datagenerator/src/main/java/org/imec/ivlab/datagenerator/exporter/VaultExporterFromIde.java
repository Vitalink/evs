package org.imec.ivlab.datagenerator.exporter;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import org.imec.ivlab.core.data.PatientKey;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;
import org.imec.ivlab.core.model.hub.Hub;
import org.imec.ivlab.core.model.hub.SearchType;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VaultExporterFromIde {


    public static void main(String[] args) throws TechnicalConnectorException {

        VaultExporterRunner exporter = new VaultExporterRunner();

        VaultExporterArguments arguments = new VaultExporterArguments();

        arguments.setTransactionType(TransactionType.SUMEHR);

        List<String> patientIDs = new ArrayList<>();
        patientIDs.add(PatientKey.PATIENT_EXAMPLE.getValue());

        arguments.setPatientKeys(patientIDs);

        arguments.setActorKey(AuthenticationConfigReader.GP_EXAMPLE);

        arguments.setValidate(true);
        arguments.setGenerateGlobalMedicationScheme(true);
        arguments.setGenerateDailyMedicationScheme(true);
        arguments.setGenerateSumehrOverview(true);
        arguments.setGenerateDiaryNoteVisualization(true);
        arguments.setGenerateVaccinationVisualization(true);
        arguments.setGenerateGatewayMedicationScheme(true);
        arguments.setDailyMedicationSchemeDate(LocalDate.now().plusYears(4));
        arguments.setFilterOutTransactionsHavingPatientAccessNo(false);

        arguments.setHub(Hub.VITALINK);
        arguments.setSearchType(SearchType.LOCAL);

//        arguments.setBreakTheGlassIfTRMissing(true);
        arguments.setExportDir(new File("../exe/exports"));

        exporter.start(arguments);


    }

}
