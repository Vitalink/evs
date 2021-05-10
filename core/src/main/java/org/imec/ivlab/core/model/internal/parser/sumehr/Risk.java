package org.imec.ivlab.core.model.internal.parser.sumehr;

import java.time.LocalDate;
import org.imec.ivlab.core.model.internal.parser.ItemParsedItem;

public class Risk extends ItemParsedItem {

    private LocalDate beginmoment;

    public LocalDate getBeginmoment() {
        return beginmoment;
    }

    public void setBeginmoment(LocalDate beginmoment) {
        this.beginmoment = beginmoment;
    }

}
