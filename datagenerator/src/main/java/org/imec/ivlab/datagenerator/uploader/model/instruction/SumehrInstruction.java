package org.imec.ivlab.datagenerator.uploader.model.instruction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.datagenerator.uploader.model.AbstractInstruction;
import org.imec.ivlab.datagenerator.uploader.model.action.SumehrAction;

@Getter
@Setter
@ToString
public class SumehrInstruction extends AbstractInstruction<SumehrAction> {

    private SumehrAction action;

    public SumehrInstruction() {
        super(TransactionType.SUMEHR);
    }

}
