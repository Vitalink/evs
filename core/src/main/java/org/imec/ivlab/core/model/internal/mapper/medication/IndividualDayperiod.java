package org.imec.ivlab.core.model.internal.mapper.medication;

import java.io.Serializable;

public class IndividualDayperiod implements Serializable {

    private Dayperiod dayperiod;

    public IndividualDayperiod(Dayperiod dayperiod) {
        this.dayperiod = dayperiod;
    }

    public Dayperiod getDayperiod() {
        return dayperiod;
    }

    public void setDayperiod(Dayperiod dayperiod) {
        this.dayperiod = dayperiod;
    }
}
