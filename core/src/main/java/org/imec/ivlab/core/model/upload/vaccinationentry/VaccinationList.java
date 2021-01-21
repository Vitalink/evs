package org.imec.ivlab.core.model.upload.vaccinationentry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.imec.ivlab.core.model.evsref.ListOfIdentifiables;
import org.imec.ivlab.core.model.upload.SpecificEntryList;

public class VaccinationList implements Serializable, ListOfIdentifiables, SpecificEntryList {

    private List<Vaccination> list = new ArrayList<>();

    public List<Vaccination> getList() {
        return list;
    }

    public void setList(List<Vaccination> list) {
        this.list = list;
    }

    @Override
    public List<Vaccination> getIdentifiables() {
        return list;
    }

}
