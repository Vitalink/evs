package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.AdministrationquantityType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.RegimenUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.List;

public class R1005_PresentAndIdenticalAdministrationUnits extends BaseMSEntryRule implements MSEntryRule {

    @Override
    public String getMessage() {
        return "When a regimen is specified, administration unit must be set and identical for all regimen entries";
    }

    @Override
    public String getRuleId() {
        return "1005";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        ItemType medicationItem = TransactionUtil.getItem(msEntry.getMseTransaction(), CDITEMvalues.MEDICATION);

        List<AdministrationquantityType> quantities = RegimenUtil.getQuantities(medicationItem.getRegimen());

        String previouslyUsedAdministrationUnitValue = null;

        if (CollectionsUtil.emptyOrNull(quantities)) {
            return passRule();
        }

        for (AdministrationquantityType quantity : quantities) {
            if (quantity == null || quantity.getUnit() == null || quantity.getUnit().getCd() == null || quantity.getUnit().getCd().getValue() == null) {
                return failRule(msEntry.getMseTransaction());
            }

            String administrationUnitValue = quantity.getUnit().getCd().getValue();

            if (previouslyUsedAdministrationUnitValue == null) {
                previouslyUsedAdministrationUnitValue = administrationUnitValue;
            } else {
                if (!StringUtils.equalsAnyIgnoreCase(previouslyUsedAdministrationUnitValue, administrationUnitValue)) {
                    return failRule(msEntry.getMseTransaction());
                }
            }

        }

        return passRule();
    }


}
