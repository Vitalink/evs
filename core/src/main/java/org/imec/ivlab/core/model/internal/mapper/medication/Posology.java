package org.imec.ivlab.core.model.internal.mapper.medication;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.AdministrationunitType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.kmehr.model.AdministrationUnit;

@Getter
@Setter
public class Posology extends PosologyOrRegimen implements Serializable {

    private String text;
    private BigDecimal posologyLow;
    private BigDecimal posologyHigh;
    private String administrationUnit;
    private BigDecimal takesLow;
    private BigDecimal takesHigh;

}
