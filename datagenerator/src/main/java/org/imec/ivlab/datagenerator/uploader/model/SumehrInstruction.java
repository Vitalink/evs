package org.imec.ivlab.datagenerator.uploader.model;

import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.upload.sumehrlist.SumehrList;

public class SumehrInstruction extends AbstractInstruction<SumehrAction> {

    private SumehrList sumehrList;
    private SumehrAction action;

    public SumehrInstruction() {
        super(TransactionType.SUMEHR);
    }

    public SumehrList getSumehrList() {
        return sumehrList;
    }

    public void setSumehrList(SumehrList sumehrList) {
        this.sumehrList = sumehrList;
    }

    @Override
    public SumehrAction getAction() {
        return action;
    }

    @Override
    public void setAction(SumehrAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "SumehrInstruction{" +
                "sumehrList=" + sumehrList +
                ", action=" + action +
                "} " + super.toString();
    }
}
