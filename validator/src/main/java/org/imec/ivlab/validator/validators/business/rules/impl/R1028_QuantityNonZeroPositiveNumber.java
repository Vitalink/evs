package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.AdministrationquantityType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.kmehr.model.util.RegimenUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.math.BigDecimal;
import java.util.List;

public class R1028_QuantityNonZeroPositiveNumber extends BaseMSEntryRule implements MSEntryRule {

    @Override
    public String getMessage() {
        return "The value of <quantity> used in a medicationschemeelement should be a non-zero positive number.";
    }

    @Override
    public String getRuleId() {
        return "1028";
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

                if (!(quantity.getDecimal().compareTo(BigDecimal.ZERO) == 1)) {
                    return failRule(transaction);
                }

            }


        }

        return passRule();
    }


}
