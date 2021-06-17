package org.imec.ivlab.core.model.internal.mapper.medication;

import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.kmehr.model.localid.LocalId;

@Getter
@Setter
public class MedicationEntry extends MedicationEntryBasic implements Serializable {

    private String medicationUse;
    private String beginCondition;
    private String endCondition;
    private LocalDate createdDate;
    private LocalTime createdTime;

    private PosologyOrRegimen posologyOrRegimen;

    private List<HcpartyType> authors;

    private List<Suspension> suspensions;

    private LocalId localId;

}
