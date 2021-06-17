package org.imec.ivlab.datagenerator.uploader.model.instruction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.datagenerator.uploader.model.AbstractInstruction;
import org.imec.ivlab.datagenerator.uploader.model.action.PopulationBasedScreeningAction;

@Getter
@Setter
@ToString
public class PopulationBasedScreeningInstruction extends AbstractInstruction<PopulationBasedScreeningAction> {

    private PopulationBasedScreeningAction action;

    public PopulationBasedScreeningInstruction() {
        super(TransactionType.POPULATION_BASED_SCREENING);
    }

}
