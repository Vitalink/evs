package org.imec.ivlab.core.model.internal.parser.vaccination;

import be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.model.internal.parser.ParsedKmehrItem;

@Getter
@Setter
public class Vaccination extends ParsedKmehrItem {

    private List<TextType> textTypes;
    private List<LnkType> linkTypes;
    private List<VaccinationItem> vaccinationItems;
    private List<EncounterLocation> encounterLocations;

    @Override
    public Set<String> ignoredNodeNames() {
        HashSet ignoredNames = new HashSet();
        ignoredNames.add("iscomplete");
        ignoredNames.add("isvalidated");
        return ignoredNames;
    }

}
