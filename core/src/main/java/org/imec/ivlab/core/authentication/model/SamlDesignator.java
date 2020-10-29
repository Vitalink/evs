package org.imec.ivlab.core.authentication.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="samlDesignator")
public class SamlDesignator {

    private String namespace;
    private String key;

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

    @Override
    public String toString() {
        return "SamlDesignator{" +
                "namespace='" + namespace + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}

