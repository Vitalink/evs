package org.imec.ivlab.core.model.internal.parser.sumehr;

import java.util.List;
import org.imec.ivlab.core.model.internal.parser.ItemParsedItem;

public class Recipient extends ItemParsedItem {

    private List<HcParty> hcParties;

    public List<HcParty> getHcParties() {
        return hcParties;
    }

    public void setHcParties(List<HcParty> hcParties) {
        this.hcParties = hcParties;
    }
}
