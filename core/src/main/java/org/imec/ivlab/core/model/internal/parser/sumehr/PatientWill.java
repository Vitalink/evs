package org.imec.ivlab.core.model.internal.parser.sumehr;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.imec.ivlab.core.model.internal.parser.ItemParsedItem;


public class PatientWill extends ItemParsedItem {

    private LocalDateTime recordDateTime;
    private LocalDate beginmoment;

    public LocalDateTime getRecordDateTime() {
        return recordDateTime;
    }

    public LocalDate getBeginmoment() {
        return beginmoment;
    }

    public void setBeginmoment(LocalDate beginmoment) {
        this.beginmoment = beginmoment;
    }

    public void setRecordDateTime(LocalDateTime recordDateTime) {
        this.recordDateTime = recordDateTime;
    }
}

