package org.imec.ivlab.datagenerator.uploader.service.scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.patient.PatientReader;
import org.imec.ivlab.core.model.patient.exceptions.PatientDataNotFoundException;
import org.imec.ivlab.core.util.FileUtil;
import org.imec.ivlab.datagenerator.uploader.dateshift.ShiftAction;
import org.imec.ivlab.datagenerator.uploader.exception.InvalidActionException;
import org.imec.ivlab.datagenerator.uploader.exception.InvalidFolderStructureException;
import org.imec.ivlab.datagenerator.uploader.exception.InvalidPatientException;
import org.imec.ivlab.datagenerator.uploader.exception.NoQueueingNeededException;
import org.imec.ivlab.datagenerator.uploader.model.AbstractInstruction;
import org.imec.ivlab.datagenerator.uploader.model.Action;
import org.imec.ivlab.datagenerator.uploader.model.DiaryNoteAction;
import org.imec.ivlab.datagenerator.uploader.model.DiaryNoteInstruction;
import org.imec.ivlab.datagenerator.uploader.model.Instruction;
import org.imec.ivlab.datagenerator.uploader.model.MSAction;
import org.imec.ivlab.datagenerator.uploader.model.MSInstruction;
import org.imec.ivlab.datagenerator.uploader.model.State;
import org.imec.ivlab.datagenerator.uploader.model.SumehrAction;
import org.imec.ivlab.datagenerator.uploader.model.SumehrInstruction;
import org.imec.ivlab.datagenerator.uploader.service.callback.impl.ExportVaultCallback;
import org.imec.ivlab.datagenerator.uploader.service.callback.impl.FileUploadCallback;
import org.imec.ivlab.datagenerator.uploader.service.callback.impl.VersionCheckCallback;
import org.imec.ivlab.datagenerator.uploader.service.queue.UploadQueueImpl;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;

public class ScannedFileHandlerImpl implements ScannedFileHandler {

    private final static Logger log = Logger.getLogger(ScannedFileHandlerImpl.class);

    private final static int ROOTFOLDER_HIERARCHY_LEVELS = 6;

    private UploadQueueImpl uploadQueue;
    private boolean exportAfterUpload;
    private boolean writeAsIs;
    private boolean validateExportAfterUpload;
    private boolean createGlobalSchemeAfterUpload;
    private boolean generateDailyMedicationScheme;
    private LocalDate dailyMedicationSchemeDate;
    private String startTransactionId;
    private ShiftAction shiftAction;
    private boolean generateSumehrOverview;
    private boolean generateGatewayMedicationScheme;
    private boolean generateDiaryNoteVisualization;

    boolean versionChecked = false;

    public ScannedFileHandlerImpl(boolean exportAfterUpload, boolean writeAsIs, boolean validateExportAfterUpload, boolean generateGlobalMedicationScheme, boolean generateDailyMedicationScheme, boolean generateSumehrOverview, LocalDate dailyMedicationSchemeDate, String startTransactionId, ShiftAction shiftAction, boolean generateGatewayMedicationScheme, boolean generateDiaryNoteVisualization) throws VitalinkException {
        this.generateSumehrOverview          = generateSumehrOverview;
        this.generateGatewayMedicationScheme = generateGatewayMedicationScheme;
        this.generateDiaryNoteVisualization  = generateDiaryNoteVisualization;
        this.uploadQueue                     = UploadQueueImpl.getInstance();
        this.startTransactionId              = startTransactionId;
        this.exportAfterUpload               = exportAfterUpload;
        this.writeAsIs                       = writeAsIs;
        this.validateExportAfterUpload       = validateExportAfterUpload;
        this.createGlobalSchemeAfterUpload   = generateGlobalMedicationScheme;
        this.generateDailyMedicationScheme   = generateDailyMedicationScheme;
        this.dailyMedicationSchemeDate       = dailyMedicationSchemeDate;
        this.startTransactionId              = startTransactionId;
        this.shiftAction                     = shiftAction;
    }



    private Instruction instructionFromFile(File rootFolder, File file) throws InvalidActionException, NoQueueingNeededException, InvalidFolderStructureException, InvalidPatientException {

        Path filePath = file.toPath();
        Path rootPath = rootFolder.toPath();
        Path relativePath = rootPath.relativize(filePath);

        String[] dirnames = new String[relativePath.getNameCount()];
        for (int i = 0; i < relativePath.getNameCount(); i++) {
            String name = relativePath.getName(i).toString();
            dirnames[i] = name;
        }

        if (dirnames.length == 0) {
            throw new InvalidFolderStructureException("Need " + ROOTFOLDER_HIERARCHY_LEVELS + " levels of subfolders within rootfolder: " + rootFolder + ". This file is not in a valid location to be uploaded: " + file.getAbsolutePath());
        }

        // check state
        String state = dirnames[0];
        if (StringUtils.equalsIgnoreCase(State.PROCESSED.getValue(), state)) {
            throw new NoQueueingNeededException("File is already processed");
        }
        if (!StringUtils.equalsIgnoreCase(State.INPUT.getValue(), state)) {
            throw new InvalidFolderStructureException("File is not in the input directory: " + file.getAbsolutePath());
        }

        if (dirnames.length < ROOTFOLDER_HIERARCHY_LEVELS) {
            throw new InvalidFolderStructureException("Need " + ROOTFOLDER_HIERARCHY_LEVELS + " levels of subfolders within rootfolder: " + rootFolder + ". This file is not in a valid location to be uploaded: " + file.getAbsolutePath());
        }

        //transaction type

        TransactionType transactionType;
        try {
            transactionType = TransactionType.fromOneLetterName(dirnames[3]);
        } catch (RuntimeException e) {
            throw new InvalidActionException("File is not in a valid transaction folder. File: " + file.getAbsolutePath() + ". Accepted transaction folders: " + StringUtils.join(TransactionType.getAllLetters(), ", "));
        }

        AbstractInstruction instruction = initiateInstruction(transactionType);

        // check patient
        try {
            instruction.setPatient(PatientReader.loadPatientByKey(dirnames[1]));
        } catch (PatientDataNotFoundException e) {
            throw new InvalidPatientException(e);
        }

        // check actor
        String actorID = dirnames[2];
        instruction.setActorID(actorID);

        Action action = initiateAction(transactionType, dirnames[4]);
        instruction.setAction(action);

        instruction.getCallbacks().add(new FileUploadCallback(rootFolder, file, instruction.getAction(), instruction.getPatient(), instruction.getActorID(), instruction.getTransactionType()));
        if (!versionChecked) {
            instruction.getCallbacks().add(new VersionCheckCallback(rootFolder));
            versionChecked = true;
        }

        instruction.setFile(file);

        instruction.setWriteAsIs(writeAsIs);


        if (MSAction.EXPORT.equals(instruction.getAction()) || exportAfterUpload || validateExportAfterUpload || createGlobalSchemeAfterUpload || generateDailyMedicationScheme || generateSumehrOverview) {

            instruction.getCallbacks().add(new ExportVaultCallback(rootFolder, file, instruction.getTransactionType(), instruction.getAction(), instruction.getPatient(), instruction.getActorID(), validateExportAfterUpload, createGlobalSchemeAfterUpload, generateDailyMedicationScheme, generateSumehrOverview, dailyMedicationSchemeDate, generateGatewayMedicationScheme, generateDiaryNoteVisualization));

        }

        if (instruction instanceof MSInstruction) {
            ((MSInstruction)instruction).setShiftAction(shiftAction);
            ((MSInstruction)instruction).setStartTransactionId(startTransactionId);
        }

        return instruction;

    }

    @Override
    public void process(File rootFolder, File file) {

        Instruction instruction = null;
        try {
            instruction = instructionFromFile(rootFolder, file);

            if (FileUtil.isLocked(file)) {
                log.error("File is locked for writing and will be ignored. Remove the lock and rename the file when done. File: " + file.getAbsolutePath());
                return;
            }

            log.info("Queued for processing: " + file.getAbsolutePath());

            uploadQueue.queue(instruction);

        } catch (InvalidActionException | InvalidFolderStructureException | InvalidPatientException e) {
            log.error(e);
        } catch (NoQueueingNeededException e) {
            // do nothing
        }

    }

    private Action initiateAction(TransactionType transactionType, String actionName) {

        Action action;
        switch (transactionType) {
            case MEDICATION_SCHEME:
                action = MSAction.fromValue(actionName);
                break;
            case SUMEHR:
                action = SumehrAction.fromValue(actionName);
                break;
            case DIARY_NOTE:
                action = DiaryNoteAction.fromValue(actionName);
                break;
            default:
                throw new RuntimeException("No action configured for transaction type: " + transactionType);
        }

        return action;

    }

    private AbstractInstruction initiateInstruction(TransactionType transactionType) {

        switch (transactionType) {
            case MEDICATION_SCHEME:
                return new MSInstruction();
            case SUMEHR:
                return new SumehrInstruction();
            case DIARY_NOTE:
                return new DiaryNoteInstruction();
            default:
                throw new RuntimeException("No instruction configured for transaction type: " + transactionType);
        }

    }


}
