package org.imec.ivlab.core.authentication.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum(String.class)
public enum Type {

    @XmlEnumValue("identification") IDENTIFICATION("identification"), @XmlEnumValue("holderofkey") HOLDEROFKEY("holderofkey"), @XmlEnumValue("encryption") ENCRYPTION("encryption");

    private String value;

    Type(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
