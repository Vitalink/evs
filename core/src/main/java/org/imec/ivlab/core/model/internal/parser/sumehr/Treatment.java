package org.imec.ivlab.core.model.internal.parser.sumehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import org.imec.ivlab.core.model.internal.parser.ItemParsedItem;

import java.time.LocalDate;

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
