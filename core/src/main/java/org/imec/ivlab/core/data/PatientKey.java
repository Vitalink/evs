package org.imec.ivlab.core.data;

public enum PatientKey {

    AXEL("axel"),
    BERT("bert"),
    KATRIEN("katrien"),
    RSB_CHRISTINE("rsb_christine"),
    RSB_LAWRENCE("rsb_lawrence"),
    RSW_DAVID("rsw_david"),
    RSW_ISABELLE("rsw_isabelle"),
    STEVEN("steven"),
    VITALINK_TESTDATA_PATIENT("vitalink_testdata_patient");

    private String value;

    PatientKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
