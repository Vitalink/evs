package org.imec.ivlab.core.model.upload;

import java.util.ArrayList;
import java.util.List;
import org.imec.ivlab.core.model.evsref.ListOfIdentifiables;

public class KmehrWithReferenceList implements ListOfIdentifiables, SpecificEntryList {

    private List<KmehrWithReference> list = new ArrayList<>();

    public List<KmehrWithReference> getList() {
        return list;
    }

    public void setList(List<KmehrWithReference> list) {
        this.list = list;
    }

    @Override
    public List<KmehrWithReference> getIdentifiables() {
        return list;
    }

}
