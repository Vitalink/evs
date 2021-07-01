package org.imec.ivlab.core.model.internal.parser.sumehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.imec.ivlab.core.model.internal.mapper.medication.Identifier;
import org.imec.ivlab.core.model.internal.parser.AbstractParsedItem;
@Getter
@Setter
public class Vaccination extends AbstractParsedItem<ItemType> {

    private String vaccinatedAgainst;
    private Identifier identifier;
    private Boolean relevant;
    private LocalDate applicationDate;
    private List<TextType> contentTextTypes;
    private List<TextType> textTypes;
    private List<CDCONTENT> cdcontents = new ArrayList<>();
    private CDLIFECYCLEvalues lifecycle;
    private LocalDateTime recordDateTime;

    public Vaccination() {
        super("item");
    }

}
