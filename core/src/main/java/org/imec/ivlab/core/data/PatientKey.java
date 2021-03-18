package org.imec.ivlab.core.data;

public enum PatientKey {

    PATIENT_EXAMPLE("example");

    private String value;

    PatientKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
