package org.imec.ivlab.datagenerator.uploader.service.queue.config;

import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;
import org.imec.ivlab.datagenerator.uploader.model.instruction.Instruction;

@SuppressWarnings({ "rawtypes"})
public interface QueueConfig<T extends Instruction> {

    public void configureUploader(T instruction);

    public void callUploadAction(T instruction, KmehrEntryList kmehrEntryList) throws UploaderException, VitalinkException;

}
