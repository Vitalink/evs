package org.imec.ivlab.core.model.internal.mapper.medication;

import java.io.Serializable;

public class RegimenDayperiod extends DayperiodOrTime implements Serializable {

    private Dayperiod dayperiod;

    public Dayperiod getDayperiod() {
        return dayperiod;
    }

    public void setDayperiod(Dayperiod dayperiod) {
        this.dayperiod = dayperiod;
    }
}
