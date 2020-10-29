package org.imec.ivlab.validator.validators.model;

import org.apache.commons.lang.ArrayUtils;
import org.imec.ivlab.validator.validators.business.rules.model.RuleResult;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    private List<RuleResult> passedList = new ArrayList<>();
    private List<RuleResult> interruptedList = new ArrayList<>();
    private List<AbstractValidationItem> failedList = new ArrayList<>();

    public List<AbstractValidationItem> getFailedListByLevel(Level... level) {

        List<AbstractValidationItem> matchingResults = new ArrayList<>();

        for (AbstractValidationItem validationItem : failedList) {
            if (ArrayUtils.indexOf(level, validationItem.getLevel()) >= 0) {
                matchingResults.add(validationItem);
            }
        }

        return matchingResults;

    }

    public List<RuleResult> getPassedList() {
        return passedList;
    }

    public List<AbstractValidationItem> getFailedList() {
        return failedList;
    }

    public List<RuleResult> getInterruptedList() {
        return interruptedList;
    }

    public int getPassedRulesCount() {
        return passedList.size();
    }

    public int getFailedRulesCount() {
        return failedList.size();
    }

    public int getInterruptedRulesCount() {
        return interruptedList.size();
    }

}
