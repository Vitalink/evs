package org.imec.ivlab.datagenerator.uploader.service.queue.config;

import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.model.upload.msentrylist.MedicationSchemeExtractor;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;
import org.imec.ivlab.datagenerator.uploader.model.instruction.MSInstruction;
import org.imec.ivlab.datagenerator.uploader.service.MSUploaderImpl;


public class MSQueueConfig implements QueueConfig<MSInstruction> {

    private MSUploaderImpl msUploader;
    private static MSQueueConfig instance = null;

    private MSQueueConfig() throws VitalinkException {
        this.msUploader = new MSUploaderImpl();
    }

    public static MSQueueConfig getInstance() throws VitalinkException {

        if(instance == null) {
            instance = new MSQueueConfig();
        }
        return instance;

    }

    @Override
    public void configureUploader(MSInstruction instruction) {

        msUploader.setStartTransactionId(instruction.getStartTransactionId());
        msUploader.setShiftAction(instruction.getShiftAction());

    }

    @Override
    public void callUploadAction(MSInstruction instruction, KmehrEntryList kmehrEntryList) throws UploaderException, VitalinkException {

        MSEntryList msEntryList;
        try {
            msEntryList = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrEntryList);
        } catch (TransformationException e) {
            throw new RuntimeException("Failed to interpret file " + instruction.getFile().getAbsolutePath(), e);
        }

        switch (instruction.getAction()) {

            case ADD:
                msUploader.add(instruction.getPatient(), msEntryList, instruction.getActorID());
                break;
            case REPLACE:
                msUploader.replace(instruction.getPatient(), msEntryList, instruction.getActorID());
                break;
            case GENERATE_REF:
                msUploader.generateREF(instruction.getPatient(), msEntryList, instruction.getActorID());
                break;
            case REMOVE_REF:
                msUploader.removeREF(instruction.getPatient(), msEntryList, instruction.getActorID());
                break;
            case UPDATE_REF:
                msUploader.updateREF(instruction.getPatient(), msEntryList, instruction.getActorID());
                break;
            case UPDATE_SCHEME_REF:
                msUploader.updateschemeREF(instruction.getPatient(), msEntryList, instruction.getActorID());
                break;
            case EXPORT:
                // do nothing, ugly hack to not upload anything here, since only the callbacks of which 'export' is one of them needs to be executed.
                break;
            default:
                throw new UploaderException("Unsupported instruction action: " + instruction.getAction());

        }

    }


}