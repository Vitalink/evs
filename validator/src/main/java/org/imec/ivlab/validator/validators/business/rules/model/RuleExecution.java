package org.imec.ivlab.validator.validators.business.rules.model;

public class RuleExecution {

    public ExecutionStatus executionStatus;
    private String transactionId;
    public String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RuleExecution(ExecutionStatus executionStatus, String message, String transactionId) {
        this.executionStatus = executionStatus;
        this.message = message;
        this.transactionId = transactionId;
    }

    public RuleExecution(ExecutionStatus executionStatus, String message) {
        this.executionStatus = executionStatus;
        this.message = message;
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
}
