package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.DurationType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.NumberUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.math.BigDecimal;

public class R1022_DurationValueNonZeroNaturalNumber extends BaseMSEntryRule implements MSEntryRule {

    @Override
    public String getMessage() {
        return "The duration value must be a non-zero natural number";
    }

    @Override
    public String getRuleId() {
        return "1022";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType transaction : msEntry.getAllTransactions()) {

            ItemType suspensionMedicationItem = TransactionUtil.getItem(transaction, CDITEMvalues.MEDICATION);

            DurationType durationType = suspensionMedicationItem.getDuration();

            if (durationType != null) {

                if (durationType.getDecimal() == null) {
                    return failRule(transaction);
                }

                if (!NumberUtil.isInteger(durationType.getDecimal()) || !(durationType.getDecimal().compareTo(BigDecimal.ZERO) == 1)) {
                    return failRule(transaction);
                }

            }

        }

        return passRule();
    }


}
