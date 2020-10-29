package org.imec.ivlab.core.model.upload.diarylist;

import org.imec.ivlab.core.model.upload.SpecificEntryList;
import org.imec.ivlab.core.model.evsref.ListOfIdentifiables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DiaryNoteList implements Serializable, ListOfIdentifiables, SpecificEntryList {

    private List<DiaryNote> list = new ArrayList<>();

    public List<DiaryNote> getList() {
        return list;
    }

    public void setList(List<DiaryNote> list) {
        this.list = list;
    }

    @Override
    public List<DiaryNote> getIdentifiables() {
        return list;
    }

}
