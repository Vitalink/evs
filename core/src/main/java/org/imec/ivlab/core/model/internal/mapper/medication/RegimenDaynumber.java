package org.imec.ivlab.core.model.internal.mapper.medication;

import java.io.Serializable;

public class RegimenDaynumber extends DaynumberOrDateOrWeekday implements Serializable {

    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
