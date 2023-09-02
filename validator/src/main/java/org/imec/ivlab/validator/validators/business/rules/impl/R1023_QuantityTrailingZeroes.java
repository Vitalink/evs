package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.AdministrationquantityType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.kmehr.model.util.RegimenUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.NumberUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.List;

public class R1023_QuantityTrailingZeroes extends BaseMSEntryRule {

    @Override
    public String getMessage() {
        return "Trailing zeros to the right of a decimal point in <quantity> should not be used";
    }

    @Override
    public String getRuleId() {
        return "1023";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType transaction : msEntry.getAllTransactions()) {

            ItemType medicationItem = TransactionUtil.getItem(transaction, CDITEMvalues.MEDICATION);

            List<AdministrationquantityType> quantities = RegimenUtil.getQuantities(medicationItem.getRegimen());

            if (CollectionsUtil.emptyOrNull(quantities)) {
                return passRule();
            }

            for (AdministrationquantityType quantity : quantities) {
                if (quantity == null || quantity.getDecimal() == null) {
                    continue;
                }

                if (NumberUtil.hasTrailingZeroes(quantity.getDecimal())) {
                    return failRule(transaction);
                }

            }


        }

        return passRule();
    }


}
