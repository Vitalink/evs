package org.imec.ivlab.core.model.internal.parser.sumehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.imec.ivlab.core.model.internal.parser.ItemParsedItem;

public class Sender extends ItemParsedItem {

    private List<HcParty> hcParties;

    public List<HcParty> getHcParties() {
        return hcParties;
    }

    public void setHcParties(List<HcParty> hcParties) {
        this.hcParties = hcParties;
    }
}
