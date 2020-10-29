package org.imec.ivlab.core.kmehr.model;

import be.fgov.ehealth.standards.kmehr.schema.v1.AdministrationquantityType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.WeekdayType;

import java.math.BigInteger;
import java.util.Date;

public class RegimenEntry {

    private BigInteger dayNumber;
    private Date date;
    private WeekdayType weekday;
    private ItemType.Regimen.Daytime daytime;
    private AdministrationquantityType quantity;

    public BigInteger getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(BigInteger dayNumber) {
        this.dayNumber = dayNumber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public WeekdayType getWeekday() {
        return weekday;
    }

    public void setWeekday(WeekdayType weekday) {
        this.weekday = weekday;
    }

    public ItemType.Regimen.Daytime getDaytime() {
        return daytime;
    }

    public void setDaytime(ItemType.Regimen.Daytime daytime) {
        this.daytime = daytime;
    }

    public AdministrationquantityType getQuantity() {
        return quantity;
    }

    public void setQuantity(AdministrationquantityType quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "RegimenEntry{" +
                "dayNumber=" + dayNumber +
                ", date=" + date +
                ", weekday=" + weekday +
                ", daytime=" + daytime +
                ", quantity=" + quantity +
                '}';
    }
}
