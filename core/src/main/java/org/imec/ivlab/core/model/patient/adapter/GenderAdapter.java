package org.imec.ivlab.core.model.patient.adapter;

import org.imec.ivlab.core.model.patient.model.Gender;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class GenderAdapter extends XmlAdapter<String, Gender> {
    public Gender unmarshal(String v) throws Exception {
        return Gender.fromValue(v);
    }

    public String marshal(Gender v) throws Exception {
        return v.getValue();
    }
}