package org.imec.ivlab.core.authentication.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="samlAttribute")
public class SamlAttribute {

    private String namespace;
    private String key;
    private String value;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SamlAttribute{" +
                "namespace='" + namespace + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
