package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.DurationType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.MomentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

public class R1020_EnddateCombinedWithDuration extends BaseMSEntryRule {

    @Override
    public String getMessage() {
        return "Endmoment and Duration cannot be used together";
    }

    @Override
    public String getRuleId() {
        return "1020";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType transaction : msEntry.getAllTransactions()) {

            ItemType suspensionMedicationItem = TransactionUtil.getItem(transaction, CDITEMvalues.MEDICATION);

            MomentType endMomentType = suspensionMedicationItem.getEndmoment();
            DurationType durationType = suspensionMedicationItem.getDuration();

            if (endMomentType != null && durationType != null) {
                return failRule(transaction);
            }

        }

        return passRule();
    }


}
