package org.imec.ivlab.core.model.internal.parser.sumehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import org.imec.ivlab.core.model.internal.parser.ItemParsedItem;

import java.util.ArrayList;
import java.util.List;

public class Risk extends ItemParsedItem {

    private List<TextType> textTypes;
    private List<CDCONTENT> cdcontents = new ArrayList<>();
    private CDLIFECYCLEvalues lifecycle;
    private Boolean isRelevant;

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

    public CDLIFECYCLEvalues getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(CDLIFECYCLEvalues lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Boolean getRelevant() {
        return isRelevant;
    }

    public void setRelevant(Boolean relevant) {
        isRelevant = relevant;
    }

}
