package org.imec.ivlab.datagenerator.uploader.service.queue.config;

import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;
import org.imec.ivlab.datagenerator.uploader.model.VaccinationInstruction;


public class VaccinationQueueConfig implements QueueConfig<VaccinationInstruction> {

    private static VaccinationQueueConfig instance = null;

    private VaccinationQueueConfig() {
    }

    public static VaccinationQueueConfig getInstance() {

        if(instance == null) {
            instance = new VaccinationQueueConfig();
        }
        return instance;

    }

    @Override
    public void configureUploader(VaccinationInstruction instruction) {
        // nothing to configure in case of SUMEHR
    }

    @Override
    public void callUploadAction(VaccinationInstruction instruction, KmehrEntryList kmehrEntryList) throws UploaderException {

        switch (instruction.getAction()) {

            case EXPORT:
                // do nothing, ugly hack to not upload anything here, since only the callbacks of which 'export' is one of them needs to be executed.
                break;
            default:
                throw new UploaderException("Unsupported instruction action: " + instruction.getAction());

        }

    }


}
