package org.imec.ivlab.core.model.evsref;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class EVSREF implements Serializable {

    public static final String FIND_EVSREF_REGULAR_EXPRESSION = "(?:===EVSREF:([^=]{3,512}?)===)";

    private String value;

    public EVSREF(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getFormatted() {
        return "===EVSREF:" + value + "===";
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EVSREF evsref = (EVSREF) o;

        return StringUtils.equalsIgnoreCase(evsref.getValue(), this.getValue());

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
