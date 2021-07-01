package org.imec.ivlab.core.model.internal.parser;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemParsedItem extends AbstractParsedItem<ItemType> {

    private CDLIFECYCLEvalues lifecycle;
    private Boolean relevant;
    private List<TextType> contentTextTypes;
    private List<TextType> textTypes;
    private List<CDCONTENT> cdcontents = new ArrayList<>();

    public ItemParsedItem() {
        super("item");
    }

}
