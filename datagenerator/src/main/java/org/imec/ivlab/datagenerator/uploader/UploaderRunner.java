package org.imec.ivlab.datagenerator.uploader;

import com.beust.jcommander.JCommander;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.config.EVSConfig;
import org.imec.ivlab.core.config.EVSProperties;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.version.LocalVersionReader;
import org.imec.ivlab.core.version.VersionManager;
import org.imec.ivlab.datagenerator.uploader.exception.ScannerException;
import org.imec.ivlab.datagenerator.uploader.service.queue.UploadQueueImpl;
import org.imec.ivlab.datagenerator.uploader.service.scanner.ScannedFileHandler;
import org.imec.ivlab.datagenerator.uploader.service.scanner.ScannedFileHandlerImpl;
import org.imec.ivlab.datagenerator.uploader.service.scanner.Scanner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploaderRunner {

    private final static Logger log = Logger.getLogger(UploaderRunner.class);

    public static void main(String[] args ) throws ScannerException, VitalinkException {

        log.info("Starting " + LocalVersionReader.getInstalledSoftwareAndVersion());
        VersionManager.printUpdateMessage();

        String[] commandlineArguments = args;
        final UploaderArguments uploaderArguments = new UploaderArguments();
        // parse the arguments
        final JCommander commander = new JCommander(uploaderArguments);
        commander.setAcceptUnknownOptions(false);
        commander.parse(commandlineArguments);

        UploaderRunner uploader = new UploaderRunner();
        uploader.start(uploaderArguments);

    }

    public void start(UploaderArguments arguments) throws ScannerException, VitalinkException {

        log.info("Starting uploader with arguments: " + arguments.toString());

        EVSConfig.getInstance().setProperty(EVSProperties.CHOSEN_HUB, arguments.getHub().name());
        EVSConfig.getInstance().setProperty(EVSProperties.SEARCH_TYPE, arguments.getSearchType().name());
        EVSConfig.getInstance().setProperty(EVSProperties.AUTO_GENERATE_KMEHR_MS_TRANSACTION_AUTHOR, String.valueOf(arguments.isAutoGenerateMSTransactionAuthor()));
        EVSConfig.getInstance().setProperty(EVSProperties.FILTER_OUT_TRANSACTIONS_HAVING_PATIENT_ACCESS_NO, String.valueOf(arguments.isFilterOutTransactionsHavingPatientAccessNo()));

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(UploadQueueImpl.getInstance());

        ScannedFileHandler scannedFileHandler = new ScannedFileHandlerImpl(arguments.isExportAfterUpload(), arguments.isWriteAsIs(), arguments.isValidateExportAfterUpload(), arguments.isGenerateGlobalMedicationScheme(), arguments.isGenerateDailyMedicationScheme(), arguments.isGenerateSumehrOverview(), arguments.getDailyMedicationSchemeDate(), arguments.getStartTransactionId(), arguments.getShiftAction(), arguments.isGenerateGatewayMedicationScheme(), arguments.isGenerateDiaryNoteVisualization(),
            arguments.isGenerateVaccinationVisualization());

        Scanner scanner = new Scanner(arguments.getRootDir(), scannedFileHandler);
        scanner.start();


    }

}
