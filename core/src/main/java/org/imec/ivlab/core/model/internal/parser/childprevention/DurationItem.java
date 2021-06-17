package org.imec.ivlab.core.model.internal.parser.childprevention;

import be.fgov.ehealth.standards.kmehr.schema.v1.UnitType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.imec.ivlab.core.model.internal.parser.ParsedContent;

@AllArgsConstructor
@Getter
public class DurationItem extends ParsedContent {

    private Long value;
    private UnitType unit;

}

