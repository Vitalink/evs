package org.imec.ivlab.datagenerator.uploader.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.datagenerator.uploader.model.action.Action;
import org.imec.ivlab.datagenerator.uploader.model.instruction.Instruction;
import org.imec.ivlab.datagenerator.uploader.service.callback.Callback;

@Getter
@Setter
@ToString
public abstract class AbstractInstruction<T extends Action> implements Instruction<T> {

    private Patient patient;
    private String actorID;
    private boolean writeAsIs;
    private List<Callback> callbacks = new ArrayList<>();
    private TransactionType transactionType;
    private File file;

    public AbstractInstruction(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
