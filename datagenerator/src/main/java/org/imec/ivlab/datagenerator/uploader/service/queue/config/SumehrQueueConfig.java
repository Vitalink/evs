package org.imec.ivlab.datagenerator.uploader.service.queue.config;

import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.extractor.SumehrListExtractor;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;
import org.imec.ivlab.datagenerator.uploader.model.instruction.SumehrInstruction;
import org.imec.ivlab.datagenerator.uploader.service.SumehrUploaderImpl;


public class SumehrQueueConfig implements QueueConfig<SumehrInstruction> {

    private SumehrUploaderImpl uploader;
    private static SumehrQueueConfig instance = null;

    private SumehrQueueConfig() throws VitalinkException {
        this.uploader = new SumehrUploaderImpl();
    }

    public static SumehrQueueConfig getInstance() throws VitalinkException {

        if(instance == null) {
            instance = new SumehrQueueConfig();
        }
        return instance;

    }

    @Override
    public void configureUploader(SumehrInstruction instruction) {
        // nothing to configure in case of SUMEHR
    }

    @Override
    public void callUploadAction(SumehrInstruction instruction, KmehrEntryList kmehrEntryList) throws UploaderException, VitalinkException {

        KmehrWithReferenceList sumehrList;
        sumehrList = new SumehrListExtractor().getKmehrWithReferenceList(kmehrEntryList);

        switch (instruction.getAction()) {

            case ADD:
                uploader.add(instruction.getPatient(), sumehrList, instruction.getActorID());
                break;
            case REPLACE:
                uploader.replace(instruction.getPatient(), sumehrList, instruction.getActorID());
                break;
            case GENERATE_REF:
                uploader.generateREF(instruction.getPatient(), instruction.getActorID());
                break;
            case REMOVE_REF:
                uploader.removeREF(instruction.getPatient(), sumehrList, instruction.getActorID());
                break;
            case EMPTY:
                uploader.empty(instruction.getPatient(), instruction.getActorID());
                break;
            case UPDATE_REF:
                uploader.updateREF(instruction.getPatient(), sumehrList, instruction.getActorID());
                break;
            case EXPORT:
                // do nothing, ugly hack to not upload anything here, since only the callbacks of which 'export' is one of them needs to be executed.
                break;
            default:
                throw new UploaderException("Unsupported instruction action: " + instruction.getAction());

        }

    }


}
