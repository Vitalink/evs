package org.imec.ivlab.core.kmehr.model.localid;

import java.io.Serializable;

public class GenericLocalId implements LocalId, Serializable {

    private String value;

    public GenericLocalId(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
