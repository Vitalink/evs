package org.imec.ivlab.core.data;

public enum RIZIVData {

    DR_VEEERLE_MOERMANS("17892144001");

    private String value;

    RIZIVData(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
