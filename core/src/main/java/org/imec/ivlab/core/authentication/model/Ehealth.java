package org.imec.ivlab.core.authentication.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="ehealth")
public class Ehealth {

    private List<String> entries;

    @XmlElement(name = "entry")
    public List<String> getEntries() {
        return entries;
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }
}
