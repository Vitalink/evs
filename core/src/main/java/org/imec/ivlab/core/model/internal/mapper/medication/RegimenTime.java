package org.imec.ivlab.core.model.internal.mapper.medication;

import java.io.Serializable;
import java.time.LocalTime;

public class RegimenTime extends DayperiodOrTime implements Serializable {

    private LocalTime time;

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
