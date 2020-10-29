package org.imec.ivlab.datagenerator.sam;

import java.math.BigDecimal;

public class Quantity {

    private BigDecimal value;
    private String unit;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
