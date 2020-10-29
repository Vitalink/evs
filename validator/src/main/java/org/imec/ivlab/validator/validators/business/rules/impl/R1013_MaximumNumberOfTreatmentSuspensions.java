package org.imec.ivlab.validator.validators.business.rules.impl;

import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

public class R1013_MaximumNumberOfTreatmentSuspensions extends BaseMSEntryRule implements MSEntryRule {

    private final static int MAXIMUM_NUMBER_OF_TREATMENT_SUSPENSIONS = 5;

    @Override
    public String getMessage() {
        return "There can be a maximum of " + MAXIMUM_NUMBER_OF_TREATMENT_SUSPENSIONS + " treatment suspensions";
    }

    @Override
    public String getRuleId() {
        return "1013";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        if (CollectionsUtil.size(msEntry.getTsTransactions()) > MAXIMUM_NUMBER_OF_TREATMENT_SUSPENSIONS) {
            return failRule();
        }

        return passRule();

    }

}
