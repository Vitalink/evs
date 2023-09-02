package org.imec.ivlab.core.model.internal.parser.sumehr;

import org.joda.time.LocalDateTime;

public class Patient extends AbstractPerson {

    private LocalDateTime recordDateTime;

    public Patient() {
        super("patient");
    }

    public LocalDateTime getRecordDateTime() {
        return recordDateTime;
    }

    public void setRecordDateTime(LocalDateTime recordDateTime) {
        this.recordDateTime = recordDateTime;
    }
}
