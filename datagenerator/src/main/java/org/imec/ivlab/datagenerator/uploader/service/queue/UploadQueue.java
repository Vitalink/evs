package org.imec.ivlab.datagenerator.uploader.service.queue;

import org.imec.ivlab.datagenerator.uploader.model.Instruction;

public interface UploadQueue {

    <T extends Instruction> void queue(T instruction);

}
