package org.imec.ivlab.datagenerator.uploader.model.instruction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.datagenerator.uploader.model.AbstractInstruction;
import org.imec.ivlab.datagenerator.uploader.model.action.ChildPreventionAction;

@Getter
@Setter
@ToString
public class ChildPreventionInstruction extends AbstractInstruction<ChildPreventionAction> {

    private ChildPreventionAction action;

    public ChildPreventionInstruction() {
        super(TransactionType.CHILD_PREVENTION);
    }

}
