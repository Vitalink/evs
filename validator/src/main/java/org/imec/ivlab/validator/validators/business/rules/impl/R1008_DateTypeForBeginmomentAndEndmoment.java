package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.MomentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

public class R1008_DateTypeForBeginmomentAndEndmoment extends BaseMSEntryRule {

    @Override
    public String getMessage() {
        return "Beginmoment and Endmoment in both medication and treatment suspension should be of type Date. Year, Yearmonth, time and text are not allowed.";
    }

    @Override
    public String getRuleId() {
        return "1008";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType transactionType : msEntry.getAllTransactions()) {
            ItemType medicationItem = TransactionUtil.getItem(transactionType, CDITEMvalues.MEDICATION);

            if (usesWrongType(medicationItem.getBeginmoment()) || usesWrongType(medicationItem.getEndmoment())) {
                return failRule(transactionType);
            }
        }

        return passRule();
    }

    private boolean usesWrongType(MomentType momentType) {

        if (momentType == null) {
            return false;
        }

        if (momentType.getText() != null || momentType.getTime() != null || momentType.getYear() != null || momentType.getYearmonth() != null) {
            return true;
        }

        return false;

    }

}
