package org.imec.ivlab.core.model.internal.parser.sumehr;

import java.time.LocalDate;
import lombok.Data;
import org.imec.ivlab.core.model.internal.parser.ItemParsedItem;

@Data
public class Treatment extends ItemParsedItem {

    private LocalDate beginmoment;
    private LocalDate endmoment;
    private boolean noKnownTreatment;

}
