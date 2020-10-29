package org.imec.ivlab.core.model.internal.mapper.medication;

import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.imec.ivlab.core.kmehr.model.localid.LocalId;

public class MedicationEntry extends MedicationEntryBasic implements Serializable {

    private String medicationUse;
    private String beginCondition;
    private String endCondition;
    private LocalDate createdDate;
    private LocalTime createdTime;

    private List<HcpartyType> authors;

    private List<Suspension> suspensions;

    private LocalId localId;

    public String getMedicationUse() {
        return medicationUse;
    }

    public void setMedicationUse(String medicationUse) {
        this.medicationUse = medicationUse;
    }

    public String getBeginCondition() {
        return beginCondition;
    }

    public void setBeginCondition(String beginCondition) {
        this.beginCondition = beginCondition;
    }

    public String getEndCondition() {
        return endCondition;
    }

    public void setEndCondition(String endCondition) {
        this.endCondition = endCondition;
    }

    public List<Suspension> getSuspensions() {
        return suspensions;
    }

    public void setSuspensions(List<Suspension> suspensions) {
        this.suspensions = suspensions;
    }

    public List<HcpartyType> getAuthors() {
        return authors;
    }

    public void setAuthors(List<HcpartyType> authors) {
        this.authors = authors;
    }

    public LocalId getLocalId() {
        return localId;
    }

    public void setLocalId(LocalId localId) {
        this.localId = localId;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalTime createdTime) {
        this.createdTime = createdTime;
    }

}
