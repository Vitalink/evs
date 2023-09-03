package org.imec.ivlab.core.model.internal.parser.diarynote;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TextWithLayoutType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.imec.ivlab.core.model.internal.parser.ParsedKmehrItem;

public class DiaryNote extends ParsedKmehrItem {

    private List<TextType> textTypes;
    private List<TextWithLayoutType> textWithLayoutTypes;
    private List<LnkType> linkTypes;
    private List<String> cdDiaryValues;
    private List<CDTRANSACTION> cdLocalEntries;

    @Override
    public Set<String> ignoredNodeNames() {
        HashSet<String> ignoredNames = new HashSet<String>();
        ignoredNames.add("iscomplete");
        ignoredNames.add("isvalidated");
        return ignoredNames;
    }

    public List<TextType> getTextTypes() {
        return textTypes;
    }

    public void setTextTypes(List<TextType> textTypes) {
        this.textTypes = textTypes;
    }

    public List<TextWithLayoutType> getTextWithLayoutTypes() {
        return textWithLayoutTypes;
    }

    public void setTextWithLayoutTypes(List<TextWithLayoutType> textWithLayoutTypes) {
        this.textWithLayoutTypes = textWithLayoutTypes;
    }

    public List<LnkType> getLinkTypes() {
        return linkTypes;
    }

    public void setLinkTypes(List<LnkType> linkTypes) {
        this.linkTypes = linkTypes;
    }

    public List<String> getCdDiaryValues() {
        return cdDiaryValues;
    }

    public void setCdDiaryValues(List<String> cdDiaryValues) {
        this.cdDiaryValues = cdDiaryValues;
    }

    public List<CDTRANSACTION> getCdLocalEntries() {
        return cdLocalEntries;
    }

    public void setCdLocalEntries(List<CDTRANSACTION> cdLocalEntries) {
        this.cdLocalEntries = cdLocalEntries;
    }
}
