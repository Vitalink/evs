package org.imec.ivlab.core.model.upload.msentrylist;

import org.imec.ivlab.core.model.upload.SpecificEntryList;
import org.imec.ivlab.core.model.evsref.ListOfIdentifiables;

import java.util.ArrayList;
import java.util.List;

public class MSEntryList implements ListOfIdentifiables, SpecificEntryList {

    private List<MSEntry> msEntries = new ArrayList<>();

    public List<MSEntry> getMsEntries() {
        return msEntries;
    }

    public void setMsEntries(List<MSEntry> msEntries) {
        this.msEntries = msEntries;
    }


    @Override
    public List<MSEntry> getIdentifiables() {
        return msEntries;
    }
}
