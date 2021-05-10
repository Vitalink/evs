package org.imec.ivlab.datagenerator.uploader;

import java.io.File;
import java.time.LocalDate;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.hub.Hub;
import org.imec.ivlab.core.model.hub.SearchType;
import org.imec.ivlab.datagenerator.uploader.dateshift.ShiftAction;
import org.imec.ivlab.datagenerator.uploader.exception.ScannerException;

public class UploaderFromIde {


    public static void main(String[] args) throws ScannerException, VitalinkException {

        UploaderRunner uploader = new UploaderRunner();

        UploaderArguments arguments = new UploaderArguments();

        arguments.setRootDir(new File("../exe/interaction"));
        arguments.setExportAfterUpload(true);

        arguments.setValidateExportAfterUpload(true);
        arguments.setWriteAsIs(false);

        arguments.setGenerateGlobalMedicationScheme(true);
        arguments.setGenerateDailyMedicationScheme(true);
        arguments.setGenerateGatewayMedicationScheme(true);
        arguments.setGenerateSumehrOverview(true);
        arguments.setGenerateDiaryNoteVisualization(true);
        arguments.setGenerateVaccinationVisualization(true);
        arguments.setDailyMedicationSchemeDate(LocalDate.now().plusYears(5));
        arguments.setAutoGenerateMSTransactionAuthor(true);
        arguments.setFilterOutTransactionsHavingPatientAccessNo(false);

        arguments.setStartTransactionId("100");

        arguments.setShiftAction(ShiftAction.NO_TAG_AND_NO_SHIFT);

        arguments.setHub(Hub.VITALINK);
        arguments.setSearchType(SearchType.GLOBAL);

        uploader.start(arguments);

    }

}