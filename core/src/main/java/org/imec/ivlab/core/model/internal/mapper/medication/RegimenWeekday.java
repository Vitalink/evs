package org.imec.ivlab.core.model.internal.mapper.medication;

import java.io.Serializable;

public class RegimenWeekday extends DaynumberOrDateOrWeekday implements Serializable {

    private Weekday weekday;

    public Weekday getWeekday() {
        return weekday;
    }

    public void setWeekday(Weekday weekday) {
        this.weekday = weekday;
    }
}
