package org.imec.ivlab.core.authentication.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="authenticationConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigDetails {

    private Evs evs;
    private Ehealth ehealth;

    public Evs getEvs() {
        return evs;
    }

    public void setEvs(Evs evs) {
        this.evs = evs;
    }

    public Ehealth getEhealth() {
        return ehealth;
    }

    public void setEhealth(Ehealth ehealth) {
        this.ehealth = ehealth;
    }
}
