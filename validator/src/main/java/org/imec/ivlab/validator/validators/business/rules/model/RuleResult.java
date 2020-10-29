package org.imec.ivlab.validator.validators.business.rules.model;

import org.imec.ivlab.validator.validators.model.AbstractValidationItem;
import org.imec.ivlab.validator.validators.model.Level;

public class RuleResult extends AbstractValidationItem {

    private String ruleId;
    private ExecutionStatus executionStatus;
    private String transactionId;

    public RuleResult(Level level, String message) {
        super(level, message);
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "RuleResult{" +
                "ruleId='" + ruleId + '\'' +
                ", executionStatus=" + executionStatus +
                ", transactionId='" + transactionId + '\'' +
                "} " + super.toString();
    }
}
