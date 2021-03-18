package org.imec.ivlab.core.data;

public enum RIZIVData {

    DR_EXAMPLE("12345678901");

    private String value;

    RIZIVData(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
