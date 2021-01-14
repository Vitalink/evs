package org.imec.ivlab.core.model.internal.parser.sumehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import org.imec.ivlab.core.model.internal.parser.ItemParsedItem;


import java.time.LocalDate;

public class Problem extends ItemParsedItem {

    private LocalDate beginmoment;
    private LocalDate endmoment;
    private CDLIFECYCLEvalues lifecycle;

    public LocalDate getBeginmoment() {
        return beginmoment;
    }

    public void setBeginmoment(LocalDate beginmoment) {
        this.beginmoment = beginmoment;
    }

    public LocalDate getEndmoment() {
        return endmoment;
    }
    public CDLIFECYCLEvalues getLifecycle() {
        return lifecycle;
    }
    public void setEndmoment(LocalDate endmoment) {
        this.endmoment = endmoment;
    }

}
