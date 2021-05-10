package org.imec.ivlab.validator.validators.business.model;

import org.apache.commons.lang.ArrayUtils;
import org.imec.ivlab.validator.validators.business.rules.model.RuleResult;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.ArrayList;
import java.util.List;

public class BusinessValidationResult {

    private String transactonId;

    private List<RuleResult> passedList = new ArrayList<>();
    private List<RuleResult> interruptedList = new ArrayList<>();
    private List<RuleResult> failedList = new ArrayList<>();

    public void registerRuleResult(RuleResult ruleResult) {

        switch (ruleResult.getExecutionStatus()) {

            case PASS:
                passedList.add(ruleResult);
                return;
            case FAIL:
                failedList.add(ruleResult);
                return;
            case INTERRUPTED:
                interruptedList.add(ruleResult);
                return;
            default:
                throw new RuntimeException("Unsupported executionstatus: " + ruleResult.getExecutionStatus());

        }

    }

    public List<RuleResult> getFailedListByRuleLevel(Level... level) {

        List<RuleResult> matchingResults = new ArrayList<>();

        for (RuleResult ruleResult : failedList) {
            if (ArrayUtils.indexOf(level, ruleResult.getLevel()) >= 0) {
                matchingResults.add(ruleResult);
            }
        }

        return matchingResults;

    }

    public List<RuleResult> getPassedList() {
        return passedList;
    }

    public List<RuleResult> getFailedList() {
        return failedList;
    }

    public List<RuleResult> getInterruptedList() {
        return interruptedList;
    }

    public String getTransactonId() {
        return transactonId;
    }

    public void setTransactonId(String transactonId) {
        this.transactonId = transactonId;
    }
}
