package org.imec.ivlab.core.model.upload.sumehrlist;

import org.imec.ivlab.core.model.upload.SpecificEntryList;
import org.imec.ivlab.core.model.evsref.ListOfIdentifiables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SumehrList implements Serializable, ListOfIdentifiables, SpecificEntryList {

    private List<Sumehr> list = new ArrayList<>();

    public List<Sumehr> getList() {
        return list;
    }

    public void setList(List<Sumehr> list) {
        this.list = list;
    }

    @Override
    public List<Sumehr> getIdentifiables() {
        return list;
    }

}
