package org.imec.ivlab.validator.validators.business.rules;

import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.validator.validators.business.rules.model.ExecutionStatus;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;

public abstract class BaseKmehrMessageRule implements KmehrMessageRule {

    protected RuleExecution passRule() {
        return new RuleExecution(ExecutionStatus.PASS, getMessage());
    }

    protected RuleExecution failRule() {

        return failRule(null);
    }

    protected RuleExecution failRule(TransactionType transactionType) {
        return new RuleExecution(ExecutionStatus.FAIL, getMessage(), TransactionUtil.getTransactionId(transactionType));
    }

    @Override
    public boolean enabled() {
        return true;
    }
}
