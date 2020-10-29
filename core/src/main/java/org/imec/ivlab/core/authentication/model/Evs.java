package org.imec.ivlab.core.authentication.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="evs")
public class Evs {

    private List<Certificate> certificates;

    @XmlElementWrapper
    @XmlElement(name="certificate")
    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }
}
