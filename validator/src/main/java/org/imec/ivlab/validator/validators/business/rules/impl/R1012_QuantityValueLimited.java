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
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.List;

public class R1012_QuantityValueLimited extends BaseMSEntryRule implements MSEntryRule {

    @Override
    public String getMessage() {
        return "If the quantity value is a natural number, the number of integer digits is limited to 4. If the quantity is a rational number, there must be one integer digit and can be a maximum of 2 fractional digits";
    }

    @Override
    public String getRuleId() {
        return "1012";
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

                if (NumberUtil.isInteger(quantity.getDecimal())) {
                    if (NumberUtil.getIntegerDigitCount(quantity.getDecimal()) > 4) {
                        return failRule(msEntry.getMseTransaction());
                    }
                } else {

                    if (NumberUtil.getIntegerDigitCount(quantity.getDecimal()) != 1) {
                        return failRule(msEntry.getMseTransaction());
                    }
                    if (NumberUtil.getFractionalDigitCount(quantity.getDecimal()) > 2) {
                        return failRule(msEntry.getMseTransaction());
                    }

                }

            }


        }

        return passRule();
    }


}
