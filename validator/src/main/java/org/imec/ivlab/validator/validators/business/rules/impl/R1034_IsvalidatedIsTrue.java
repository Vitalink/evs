package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;


public class R1034_IsvalidatedIsTrue extends BaseMSEntryRule {


    @Override
    public String getMessage() {
        return "The element <isvalidated> should be true for all transactions";
    }

    @Override
    public String getRuleId() {
        return "1034";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType transactionType : msEntry.getAllTransactions()) {

            if (!transactionType.isIsvalidated()) {
                return failRule(transactionType);
            }

        }

        return passRule();
    }

}
