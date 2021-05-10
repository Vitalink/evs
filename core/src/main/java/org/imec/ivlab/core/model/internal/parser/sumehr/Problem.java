package org.imec.ivlab.core.model.internal.parser.sumehr;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
