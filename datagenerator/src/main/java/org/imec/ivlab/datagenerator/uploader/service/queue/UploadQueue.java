package org.imec.ivlab.datagenerator.uploader.service.queue;

import org.imec.ivlab.datagenerator.uploader.model.instruction.Instruction;

public interface UploadQueue {

    @SuppressWarnings({ "rawtypes"})
    <T extends Instruction> void queue(T instruction);

}
