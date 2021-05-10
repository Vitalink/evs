package org.imec.ivlab.core.model.internal.parser.sumehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTEMPORALITYvalues;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.imec.ivlab.core.model.internal.mapper.medication.MedicationEntryBasic;

public class MedicationEntrySumehr extends MedicationEntryBasic {

    private CDLIFECYCLEvalues lifecycle;
    private Boolean isRelevant;
    private List<CDCONTENT> cdcontents = new ArrayList<>();
    private CDTEMPORALITYvalues temporality;
    private LocalDateTime recordDateTime;

    public CDLIFECYCLEvalues getLifecycle() {
        return lifecycle;
    }
    public CDTEMPORALITYvalues getTemporality(){ return temporality;}

    public void setLifecycle(CDLIFECYCLEvalues lifecycle) {
        this.lifecycle = lifecycle;
    }
    public void setTemporality(CDTEMPORALITYvalues temporality) {
        this.temporality = temporality;
    }

    public Boolean getRelevant() {
        return isRelevant;
    }

    public void setRelevant(Boolean relevant) {
        isRelevant = relevant;
    }

    public List<CDCONTENT> getCdcontents() {
        return cdcontents;
    }

    public void setCdcontents(List<CDCONTENT> cdcontents) {
        this.cdcontents = cdcontents;
    }

    public LocalDateTime getRecordDateTime() {
        return recordDateTime;
    }

    public void setRecordDateTime(LocalDateTime recordDateTime) {
        this.recordDateTime = recordDateTime;
    }
}
