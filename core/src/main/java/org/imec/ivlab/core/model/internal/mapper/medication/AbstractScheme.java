package org.imec.ivlab.core.model.internal.mapper.medication;

import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.util.CollectionsUtil;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import java.util.List;

public abstract class AbstractScheme {

    private Patient patient;
    private String version;
    private List<HcpartyType> authors;
    private LocalDate lastModifiedDate;
    private LocalTime lastModifiedTime;

    List<MedicationEntry> medicationEntries;

    public List<MedicationEntry> getMedicationEntries() {
        return medicationEntries;
    }

    public void setMedicationEntries(List<MedicationEntry> medicationEntries) {
        this.medicationEntries = medicationEntries;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<HcpartyType> getAuthors() {
        return authors;
    }

    public void setAuthors(List<HcpartyType> authors) {
        this.authors = authors;
    }

    public LocalDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LocalTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(LocalTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public int getMedicationCount() {
        if (CollectionsUtil.emptyOrNull(medicationEntries)) {
            return 0;
        } else {
            return medicationEntries.size();
        }
    }

    public int getSuspensionsCount() {
        if (CollectionsUtil.emptyOrNull(medicationEntries)) {
            return 0;
        } else {
            int suspensionCount = 0;
            for (MedicationEntry medicationEntry : medicationEntries) {
                if (CollectionsUtil.notEmptyOrNull(medicationEntry.getSuspensions())) {
                    suspensionCount += medicationEntry.getSuspensions().size();
                }
            }
            return suspensionCount;
        }
    }
}
