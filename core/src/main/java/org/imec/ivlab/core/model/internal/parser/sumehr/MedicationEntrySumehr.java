package org.imec.ivlab.core.model.internal.parser.sumehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTEMPORALITYvalues;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.model.internal.mapper.medication.MedicationEntryBasic;
import org.imec.ivlab.core.model.internal.mapper.medication.Posology;
import org.imec.ivlab.core.model.internal.mapper.medication.Regimen;

@Getter
@Setter
public class MedicationEntrySumehr extends MedicationEntryBasic {

    private CDLIFECYCLEvalues lifecycle;
    private Boolean relevant;
    private List<CDCONTENT> cdcontents = new ArrayList<>();
    private CDTEMPORALITYvalues temporality;
    private LocalDateTime recordDateTime;

    private Posology posology;
    private Regimen regimen;

    public CDLIFECYCLEvalues getLifecycle() {
        return lifecycle;
    }
    public CDTEMPORALITYvalues getTemporality(){ return temporality;}

}
