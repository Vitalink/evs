package org.imec.ivlab.datagenerator.uploader.service.queue.config;

import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;
import org.imec.ivlab.datagenerator.uploader.model.instruction.ChildPreventionInstruction;


public class ChildPreventionQueueConfig implements QueueConfig<ChildPreventionInstruction> {

    private static ChildPreventionQueueConfig instance = null;

    private ChildPreventionQueueConfig() {
    }

    public static ChildPreventionQueueConfig getInstance() {

        if(instance == null) {
            instance = new ChildPreventionQueueConfig();
        }
        return instance;

    }

    @Override
    public void configureUploader(ChildPreventionInstruction instruction) {
        // nothing to configure in case of SUMEHR
    }

    @Override
    public void callUploadAction(ChildPreventionInstruction instruction, KmehrEntryList kmehrEntryList) throws UploaderException {

        switch (instruction.getAction()) {

            case EXPORT:
                // do nothing, ugly hack to not upload anything here, since only the callbacks of which 'export' is one of them needs to be executed.
                break;
            default:
                throw new UploaderException("Unsupported instruction action: " + instruction.getAction());

        }

    }


}
