package org.imec.ivlab.core.model.internal.mapper.medication;

import be.ehealth.technicalconnector.adapter.XmlTimeNoTzAdapter;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
    @XmlJavaTypeAdapter(XmlTimeNoTzAdapter.class)
    private DateTime createdTime;

    private PosologyOrRegimen posologyOrRegimen;

    private List<HcpartyType> authors;

    private List<Suspension> suspensions;

    private LocalId localId;

}
