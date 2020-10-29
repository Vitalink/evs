package org.imec.ivlab.core.model.internal.parser;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;

import java.util.ArrayList;
import java.util.List;

public class ItemParsedItem extends AbstractParsedItem<ItemType> {

    private CDLIFECYCLEvalues lifecycle;
    private Boolean isRelevant;
    private List<TextType> textTypes;
    private List<CDCONTENT> cdcontents = new ArrayList<>();

    public ItemParsedItem() {
        super("item");
    }

    public CDLIFECYCLEvalues getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(CDLIFECYCLEvalues lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Boolean isRelevant() {
        return isRelevant;
    }

    public void setRelevant(Boolean relevant) {
        isRelevant = relevant;
    }

    public List<TextType> getTextTypes() {
        return textTypes;
    }

    public void setTextTypes(List<TextType> textTypes) {
        this.textTypes = textTypes;
    }

    public List<CDCONTENT> getCdcontents() {
        return cdcontents;
    }

    public void setCdcontents(List<CDCONTENT> cdcontents) {
        this.cdcontents = cdcontents;
    }

    public Boolean getRelevant() {
        return isRelevant;
    }
}
