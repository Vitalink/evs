package org.imec.ivlab.datagenerator.uploader.model.instruction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.datagenerator.uploader.model.AbstractInstruction;
import org.imec.ivlab.datagenerator.uploader.model.action.VaccinationAction;

@Getter
@Setter
@ToString
public class VaccinationInstruction extends AbstractInstruction<VaccinationAction> {

    private VaccinationAction action;

    public VaccinationInstruction() {
        super(TransactionType.VACCINATION);
    }

}
