package org.imec.ivlab.core.model.internal.parser.childprevention;

import be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.model.internal.parser.ParsedKmehrItem;

@Getter
@Setter
public class ChildPrevention extends ParsedKmehrItem {

    private DurationItem pregnancyDuration;
    private TextItem resultHearingScreeningLeft;
    private TextItem resultHearingScreeningRight;
    private BooleanItem refusalHearingScreening;
    private BooleanItem pregnancyCMVInfection;
    private BooleanItem bacterialMeningitis;
    private BooleanItem severeHeadTrauma;
    private LnkType childPreventionFile;

    @Override
    public Set<String> ignoredNodeNames() {
        HashSet<String> ignoredNames = new HashSet<String>();
        ignoredNames.add("iscomplete");
        ignoredNames.add("isvalidated");
        return ignoredNames;
    }

}
