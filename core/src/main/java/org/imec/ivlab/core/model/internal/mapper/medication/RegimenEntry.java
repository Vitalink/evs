package org.imec.ivlab.core.model.internal.mapper.medication;

import java.io.Serializable;
import java.math.BigDecimal;

public class RegimenEntry implements Serializable {

    private DaynumberOrDateOrWeekday daynumberOrDateOrWeekday;
    private DayperiodOrTime dayperiodOrTime;
    private BigDecimal quantity;

    public DaynumberOrDateOrWeekday getDaynumberOrDateOrWeekday() {
        return daynumberOrDateOrWeekday;
    }

    public void setDaynumberOrDateOrWeekday(DaynumberOrDateOrWeekday daynumberOrDateOrWeekday) {
        this.daynumberOrDateOrWeekday = daynumberOrDateOrWeekday;
    }

    public DayperiodOrTime getDayperiodOrTime() {
        return dayperiodOrTime;
    }

    public void setDayperiodOrTime(DayperiodOrTime dayperiodOrTime) {
        this.dayperiodOrTime = dayperiodOrTime;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }



    public boolean appliesToSameDay(RegimenEntry otherRegimenEntry) {
        DaynumberOrDateOrWeekday otherDaynumberOrDateOrWeekday = otherRegimenEntry.getDaynumberOrDateOrWeekday();

        if (this.daynumberOrDateOrWeekday instanceof RegimenDate && otherDaynumberOrDateOrWeekday instanceof RegimenDate) {
            if (((RegimenDate) this.daynumberOrDateOrWeekday).getDate().equals(((RegimenDate) otherDaynumberOrDateOrWeekday).getDate())) {
                return true;
            }
        } else  if (this.daynumberOrDateOrWeekday instanceof RegimenDaynumber && otherDaynumberOrDateOrWeekday instanceof RegimenDaynumber) {
            if (((RegimenDaynumber) this.daynumberOrDateOrWeekday).getNumber() == (((RegimenDaynumber) otherDaynumberOrDateOrWeekday).getNumber())) {
                return true;
            }
        } else  if (this.daynumberOrDateOrWeekday instanceof RegimenWeekday && otherDaynumberOrDateOrWeekday instanceof RegimenWeekday) {
            if (((RegimenWeekday) this.daynumberOrDateOrWeekday).getWeekday() == (((RegimenWeekday) otherDaynumberOrDateOrWeekday).getWeekday())) {
                return true;
            }
        }

        if (this.daynumberOrDateOrWeekday == null && otherDaynumberOrDateOrWeekday == null) {
            return true;
        }

        return false;

    }
}
