package org.imec.ivlab.core.model.internal.parser.diarynote;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TextWithLayoutType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;
import org.imec.ivlab.core.model.internal.parser.ParsedKmehrItem;

@Data
public class DiaryNote extends ParsedKmehrItem {

    private List<TextType> textTypes;
    private List<TextWithLayoutType> textWithLayoutTypes;
    private List<LnkType> linkTypes;
    private List<String> cdDiaryValues;
    private List<CDTRANSACTION> cdLocalEntries;

    @Override
    public Set<String> ignoredNodeNames() {
        HashSet ignoredNames = new HashSet();
        ignoredNames.add("iscomplete");
        ignoredNames.add("isvalidated");
        return ignoredNames;
    }

}
