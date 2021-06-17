package org.imec.ivlab.core.model.internal.parser.populationbasedscreening;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.model.internal.parser.ParsedKmehrItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.BooleanItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.DateItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.TextItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.YearItem;

@Getter
@Setter
public class PopulationBasedScreening extends ParsedKmehrItem {

    private YearItem screeningYear;
    private TextItem screeningType;
    private TextItem invitationType;
    private DateItem invitationDate;
    private TextItem invitationLocationName;
    private TextItem invitationLocationAddress;
    private DateItem participationDate;
    private TextItem participationLocationName;
    private TextItem participationLocationAddress;
    private TextItem participationResult;
    private BooleanItem followupNeeded;
    private TextItem followupAdvice;
    private BooleanItem followupApproved;
    private TextItem nextInvitationIndication;

    @Override
    public Set<String> ignoredNodeNames() {
        HashSet ignoredNames = new HashSet();
        ignoredNames.add("iscomplete");
        ignoredNames.add("isvalidated");
        return ignoredNames;
    }

}
