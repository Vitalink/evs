package org.imec.ivlab.core.model.internal.mapper.medication;

import java.io.Serializable;
import java.time.LocalDate;

public class RegimenDate extends DaynumberOrDateOrWeekday implements Serializable {

    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
