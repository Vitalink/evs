package org.imec.ivlab.validator.validators.business.rules;

import org.imec.ivlab.validator.validators.business.rules.model.ExecutionStatus;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;

public abstract class BaseKmehrMessageStringRule implements KmehrMessageStringRule {

    protected RuleExecution passRule() {
        return new RuleExecution(ExecutionStatus.PASS, getMessage());
    }

    protected RuleExecution failRule() {

        return new RuleExecution(ExecutionStatus.FAIL, getMessage());
    }

    @Override
    public boolean enabled() {
        return true;
    }
}
