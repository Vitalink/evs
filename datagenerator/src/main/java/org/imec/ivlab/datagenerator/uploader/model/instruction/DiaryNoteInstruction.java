package org.imec.ivlab.datagenerator.uploader.model.instruction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.datagenerator.uploader.model.AbstractInstruction;
import org.imec.ivlab.datagenerator.uploader.model.action.DiaryNoteAction;

@Getter
@Setter
@ToString
public class DiaryNoteInstruction extends AbstractInstruction<DiaryNoteAction> {

    private DiaryNoteAction action;

    public DiaryNoteInstruction() {
        super(TransactionType.DIARY_NOTE);
    }


}
