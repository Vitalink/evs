package org.imec.ivlab.core.model.internal.parser.sumehr;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.model.internal.parser.ItemParsedItem;

@Getter
@Setter
public class Problem extends ItemParsedItem {

    private LocalDateTime recordDateTime;
    private LocalDate beginmoment;
    private LocalDate endmoment;



}
