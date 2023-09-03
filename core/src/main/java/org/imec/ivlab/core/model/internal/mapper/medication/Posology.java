package org.imec.ivlab.core.model.internal.mapper.medication;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Posology extends PosologyOrRegimen {

    private String text;
    private BigDecimal posologyLow;
    private BigDecimal posologyHigh;
    private String administrationUnit;
    private BigDecimal takesLow;
    private BigDecimal takesHigh;

}
