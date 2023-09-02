package org.imec.ivlab.core.model.internal.parser.sumehr;

import org.imec.ivlab.core.model.internal.parser.ItemParsedItem;

import org.joda.time.LocalDate;

public class Treatment extends ItemParsedItem {

    private LocalDate beginmoment;
    private LocalDate endmoment;

    public LocalDate getBeginmoment() {
        return beginmoment;
    }

    public void setBeginmoment(LocalDate beginmoment) {
        this.beginmoment = beginmoment;
    }

    public LocalDate getEndmoment() {
        return endmoment;
    }
    
    public void setEndmoment(LocalDate endmoment) {
        this.endmoment = endmoment;
    }

}
