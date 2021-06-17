package org.imec.ivlab.datagenerator.uploader.service.queue.config;

import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;
import org.imec.ivlab.datagenerator.uploader.model.instruction.PopulationBasedScreeningInstruction;


public class PopulationBasedScreeningQueueConfig implements QueueConfig<PopulationBasedScreeningInstruction> {

    private static PopulationBasedScreeningQueueConfig instance = null;

    private PopulationBasedScreeningQueueConfig() {
    }

    public static PopulationBasedScreeningQueueConfig getInstance() {

        if(instance == null) {
            instance = new PopulationBasedScreeningQueueConfig();
        }
        return instance;

    }

    @Override
    public void configureUploader(PopulationBasedScreeningInstruction instruction) {
        // nothing to configure
    }

    @Override
    public void callUploadAction(PopulationBasedScreeningInstruction instruction, KmehrEntryList kmehrEntryList) throws UploaderException {

        switch (instruction.getAction()) {

            case EXPORT:
                // do nothing, ugly hack to not upload anything here, since only the callbacks of which 'export' is one of them needs to be executed.
                break;
            default:
                throw new UploaderException("Unsupported instruction action: " + instruction.getAction());

        }

    }


}
