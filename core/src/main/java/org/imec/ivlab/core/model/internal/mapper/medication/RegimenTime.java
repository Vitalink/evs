package org.imec.ivlab.core.model.internal.mapper.medication;

import java.io.Serializable;

import be.ehealth.technicalconnector.adapter.XmlTimeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

// Inspired by what eHealth did for HeaderType time issue

public class RegimenTime extends DayperiodOrTime implements Serializable {

    @XmlJavaTypeAdapter(XmlTimeAdapter.class)
    private DateTime time;

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }
}
