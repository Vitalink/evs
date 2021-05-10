package org.imec.ivlab.datagenerator.uploader.model;

import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.upload.diarylist.DiaryNoteList;

public class DiaryNoteInstruction extends AbstractInstruction<DiaryNoteAction> {

    private DiaryNoteList diaryNoteList;
    private DiaryNoteAction action;

    public DiaryNoteInstruction() {
        super(TransactionType.DIARY_NOTE);
    }

    public DiaryNoteList getDiaryNoteList() {
        return diaryNoteList;
    }

    public void setDiaryNoteList(DiaryNoteList diaryNoteList) {
        this.diaryNoteList = diaryNoteList;
    }

    @Override
    public DiaryNoteAction getAction() {
        return action;
    }

    @Override
    public void setAction(DiaryNoteAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "DiaryNoteInstruction{" +
                "diaryNoteList=" + diaryNoteList +
                ", action=" + action +
                "} " + super.toString();
    }

}
