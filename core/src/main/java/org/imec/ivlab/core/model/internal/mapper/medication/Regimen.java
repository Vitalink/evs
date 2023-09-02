package org.imec.ivlab.core.model.internal.mapper.medication;

import java.util.List;

public class Regimen extends PosologyOrRegimen {

    private String administrationUnit;

    private List<RegimenEntry> entries;

    public String getAdministrationUnit() {
        return administrationUnit;
    }

    public void setAdministrationUnit(String administrationUnit) {
        this.administrationUnit = administrationUnit;
    }

    public List<RegimenEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<RegimenEntry> entries) {
        this.entries = entries;
    }
}
