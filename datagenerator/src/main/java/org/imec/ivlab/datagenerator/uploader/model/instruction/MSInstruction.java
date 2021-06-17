package org.imec.ivlab.datagenerator.uploader.model.instruction;

import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.datagenerator.uploader.dateshift.ShiftAction;
import org.imec.ivlab.datagenerator.uploader.model.AbstractInstruction;
import org.imec.ivlab.datagenerator.uploader.model.action.MSAction;

public class MSInstruction extends AbstractInstruction<MSAction> {

    private MSEntryList msEntryList;
    private ShiftAction shiftAction;
    private String startTransactionId;
    private MSAction action;

    public MSInstruction() {
        super(TransactionType.MEDICATION_SCHEME);
    }

    public MSEntryList getMsEntryList() {
        return msEntryList;
    }

    public void setMsEntryList(MSEntryList msEntryList) {
        this.msEntryList = msEntryList;
    }

    public String getStartTransactionId() {
        return startTransactionId;
    }

    public void setStartTransactionId(String startTransactionId) {
        this.startTransactionId = startTransactionId;
    }


    public ShiftAction getShiftAction() {
        return shiftAction;
    }

    public void setShiftAction(ShiftAction shiftAction) {
        this.shiftAction = shiftAction;
    }

    @Override
    public MSAction getAction() {
        return action;
    }

    @Override
    public void setAction(MSAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "MSInstruction{" +
                "msEntryList=" + msEntryList +
                ", shiftAction=" + shiftAction +
                ", startTransactionId='" + startTransactionId + '\'' +
                ", action=" + action +
                "} " + super.toString();
    }

}
