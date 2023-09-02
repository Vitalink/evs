package org.imec.ivlab.core.model.internal.parser.childprevention;

import org.joda.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.imec.ivlab.core.model.internal.parser.ParsedContent;

@AllArgsConstructor
@Getter
public class DateItem extends ParsedContent {

    private LocalDate date;

}

