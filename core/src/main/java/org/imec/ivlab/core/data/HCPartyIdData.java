package org.imec.ivlab.core.data;

public enum HCPartyIdData {

    FREQUENTLY_USED_HCPARTY_ID("17892144001");

    private String value;

    HCPartyIdData(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
