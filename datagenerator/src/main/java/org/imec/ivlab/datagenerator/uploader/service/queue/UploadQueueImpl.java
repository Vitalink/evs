package org.imec.ivlab.datagenerator.uploader.service.queue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.ConsoleUtils;
import org.imec.ivlab.core.version.VersionManager;
import org.imec.ivlab.datagenerator.uploader.exception.CallbackException;
import org.imec.ivlab.datagenerator.uploader.model.instruction.ChildPreventionInstruction;
import org.imec.ivlab.datagenerator.uploader.model.instruction.DiaryNoteInstruction;
import org.imec.ivlab.datagenerator.uploader.model.instruction.Instruction;
import org.imec.ivlab.datagenerator.uploader.model.instruction.MSInstruction;
import org.imec.ivlab.datagenerator.uploader.model.instruction.PopulationBasedScreeningInstruction;
import org.imec.ivlab.datagenerator.uploader.model.instruction.SumehrInstruction;
import org.imec.ivlab.datagenerator.uploader.model.instruction.VaccinationInstruction;
import org.imec.ivlab.datagenerator.uploader.service.callback.Callback;
import org.imec.ivlab.datagenerator.uploader.service.queue.config.ChildPreventionQueueConfig;
import org.imec.ivlab.datagenerator.uploader.service.queue.config.DiaryNoteQueueConfig;
import org.imec.ivlab.datagenerator.uploader.service.queue.config.MSQueueConfig;
import org.imec.ivlab.datagenerator.uploader.service.queue.config.PopulationBasedScreeningQueueConfig;
import org.imec.ivlab.datagenerator.uploader.service.queue.config.QueueConfig;
import org.imec.ivlab.datagenerator.uploader.service.queue.config.SumehrQueueConfig;
import org.imec.ivlab.datagenerator.uploader.service.queue.config.VaccinationQueueConfig;

public class UploadQueueImpl implements Runnable, UploadQueue {

    private final static Logger LOG = LogManager.getLogger(UploadQueueImpl.class);

    private final static int WAIT_TIME_IN_MILLIS_AFTER_QUEUE_IS_EMPTY = 3000;

    @SuppressWarnings({ "rawtypes"})
    private CopyOnWriteArrayList<Instruction> instructions = new CopyOnWriteArrayList<>();

    private static UploadQueueImpl instance = null;

    private UploadQueueImpl() {};

    public static UploadQueueImpl getInstance() throws VitalinkException {

        if(instance == null) {
            instance = new UploadQueueImpl();
        }

        return instance;

    }

    @Override
    @SuppressWarnings({ "rawtypes"})
    public void queue(Instruction instruction) {
            instructions.add(instruction);
            LOG.info("Added instruction to queue: " + instruction.toString());
    }

    @SuppressWarnings({ "rawtypes"})
    private QueueConfig getUploadQueueConfig(Instruction instruction) throws VitalinkException {

        if (instruction instanceof MSInstruction) {
            return MSQueueConfig.getInstance();
        } else if (instruction instanceof SumehrInstruction) {
            return SumehrQueueConfig.getInstance();
        } else if (instruction instanceof DiaryNoteInstruction) {
            return DiaryNoteQueueConfig.getInstance();
        } else if (instruction instanceof VaccinationInstruction) {
            return VaccinationQueueConfig.getInstance();
        } else if (instruction instanceof ChildPreventionInstruction) {
            return ChildPreventionQueueConfig.getInstance();
        } else if (instruction instanceof PopulationBasedScreeningInstruction) {
            return PopulationBasedScreeningQueueConfig.getInstance();
        }

        throw new RuntimeException("No upload queue configured for instruction: " + instruction.getClass());

    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked"})
    public void run() {

        while (true) {

            Iterator<Instruction> instructionIterator = instructions.iterator();

            while (instructionIterator.hasNext()) {

                Instruction instruction = instructionIterator.next();

                boolean success = true;

                try {
                    QueueConfig uploadQueueConfig = getUploadQueueConfig(instruction);

                    LOG.info(System.lineSeparator() + ConsoleUtils.emphasizeTitle("EVS instruction started for action " + instruction.getAction().getName() + " and file " + instruction.getFile().getName()));
                    LOG.info("Instruction details: " + instruction.toString());

                    uploadQueueConfig.configureUploader(instruction);
                    KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(instruction.getFile());
                    LOG.info("Processing instruction: " + instruction);
                    uploadQueueConfig.callUploadAction(instruction, kmehrEntryList);

                    if (CollectionsUtil.notEmptyOrNull(instruction.getCallbacks())) {
                        for (Object o : instruction.getCallbacks()) {
                            Callback callback = (Callback) o;
                            try {
                                callback.pass();
                            } catch (CallbackException e) {
                                LOG.error(ExceptionUtils.getStackTrace(e));
                                success = false;
                            }
                        }

                    }

                } catch (Throwable t) {

                    success = false;

                    LOG.error("Exception while executing instruction: " + instruction.toString() +
                            System.lineSeparator() + "Error details: " + ExceptionUtils.getStackTrace(t));

                    LOG.info("Upload instruction failed. Will send callback");
                    if (CollectionsUtil.notEmptyOrNull(instruction.getCallbacks())) {
                        for (Object o : instruction.getCallbacks()) {
                            Callback callback = (Callback) o;
                            try {
                                callback.fail(ExceptionUtils.getStackTrace(t));
                            } catch (CallbackException e) {
                                LOG.error(ExceptionUtils.getStackTrace(e));
                            }
                        }
                    }

                }

                if (success) {
                    LOG.info(System.lineSeparator() + ConsoleUtils.emphasizeTitle(ConsoleUtils.createAsciiMessage("SUCCESS")) + ConsoleUtils.emphasizeTitle("EVS instruction completed successfully for action: " + instruction.getAction() + " and file " + instruction.getFile().getName()));
                } else {
                    LOG.info(System.lineSeparator() + ConsoleUtils.emphasizeTitle(ConsoleUtils.createAsciiMessage("ERROR")) + ConsoleUtils.emphasizeTitle("EVS instruction completed with errors for action: " + instruction.getAction() + " and file " + instruction.getFile().getName()));
                }


                List<Instruction> instructionsToDeQueue = new ArrayList<>();
                instructionsToDeQueue.add(instruction);
                instructions.removeAll(instructionsToDeQueue);

                VersionManager.printUpdateMessage();

            }

            try {
                Thread.sleep(WAIT_TIME_IN_MILLIS_AFTER_QUEUE_IS_EMPTY);
            } catch (InterruptedException unimportantException) {
            }

        }

    }

}
