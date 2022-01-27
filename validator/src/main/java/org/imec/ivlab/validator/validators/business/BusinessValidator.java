package org.imec.ivlab.validator.validators.business;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.model.upload.msentrylist.MedicationSchemeExtractor;
import org.imec.ivlab.core.util.ClassesUtil;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.model.BusinessValidationResult;
import org.imec.ivlab.validator.validators.business.rules.CustomMessage;
import org.imec.ivlab.validator.validators.business.rules.KmehrMessageRule;
import org.imec.ivlab.validator.validators.business.rules.KmehrMessageStringRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.Rule;
import org.imec.ivlab.validator.validators.business.rules.model.ExecutionStatus;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.business.rules.model.RuleResult;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.ArrayList;
import java.util.List;

public class BusinessValidator {

    private static Logger LOG = Logger.getLogger(BusinessValidator.class);
    private boolean skipDisabledRules = false;

    private List<String> ruleIdIgnoreList = new ArrayList<>();

    public BusinessValidator() {
    }

    public BusinessValidator(List<String> ruleIdIgnoreList) {
        this.ruleIdIgnoreList = ruleIdIgnoreList;
    }

    public BusinessValidationResult validate(Kmehrmessage kmehrmessage, String kmehrmessageString) {

        BusinessValidationResult validationResult = new BusinessValidationResult();

        List<RuleResult> ruleResults = executeRules(kmehrmessage, kmehrmessageString);

        for (RuleResult ruleResult : ruleResults) {
            validationResult.registerRuleResult(ruleResult);
        }

        return validationResult;

    }

    private List<RuleResult> executeRules(Kmehrmessage kmehrmessage, String kmehrmessageString) {

        List<RuleResult> results = new ArrayList<>();

        MSEntryList msEntryList = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrmessage);

        List<Class<Rule>> ruleClasses = ClassesUtil.getClasses("org.imec.ivlab.validator", Rule.class);

        for (Class<Rule> ruleClass : ruleClasses) {

            Rule ruleInstance = null;
            try {
                ruleInstance = ruleClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Failed to instantiate rule " + ruleClass.getName(), e);
            }

            if (!skipDisabledRules && !ruleInstance.enabled()) {
                LOG.info(String.format("Skipping disabled rule %s - %s", ruleInstance.getRuleId(), ruleInstance.getMessage()));
                continue;
            }

            if (ruleInstance instanceof KmehrMessageRule) {
                addResultIfNotIgnored(results, executeKmehrMessageRule((KmehrMessageRule) ruleInstance, kmehrmessage));
            } else if (ruleInstance instanceof KmehrMessageStringRule) {
                addResultIfNotIgnored(results, executeKmehrMessageStringRule((KmehrMessageStringRule) ruleInstance, kmehrmessageString));
            } else if (ruleInstance instanceof MSEntryRule) {
                if (msEntryList != null && CollectionsUtil.notEmptyOrNull(msEntryList.getMsEntries())) {
                    for (MSEntry msEntry : msEntryList.getMsEntries()) {
                        addResultIfNotIgnored(results, executeMSEntryRule((MSEntryRule) ruleInstance, msEntry));
                    }
                }
            } else {
                results.add(new RuleResult(Level.FATAL, "Unsupported rule: " + ruleInstance.getClass().getName()));
            }
        }

        return results;

    }

    private void addResultIfNotIgnored(List<RuleResult> results, RuleResult ruleResult) {
        if (CollectionsUtil.notEmptyOrNull(ruleIdIgnoreList) && (ExecutionStatus.FAIL.equals(ruleResult.getExecutionStatus()))) {
            for (String ruleIdToIgnore : ruleIdIgnoreList) {
                if (ruleResult != null && StringUtils.isNotEmpty(ruleIdToIgnore) && StringUtils.containsIgnoreCase(ruleResult.getRuleId(), ruleIdToIgnore)) {
                    LOG.info("Ignoring rule result because it was specified in the ruleIdIgnoreList. Ignored rule result: " + ruleResult.toString());
                    return;
                }
            }
        }
        results.add(ruleResult);
    }

    private <T extends Rule> RuleResult executeMSEntryRule(MSEntryRule msEntryRule, MSEntry msEntry) {

        RuleExecution ruleExecution = executeRule(msEntryRule, msEntry);

        String ruleExecutionMessage = ruleExecution.getMessage();
        if (msEntryRule instanceof CustomMessage) {
            CustomMessage customMessage = (CustomMessage) msEntryRule;
            ruleExecutionMessage = ruleExecutionMessage + customMessage.getCustomMessage();
        }
        RuleResult ruleResult = new RuleResult(msEntryRule.getLevel(), ruleExecutionMessage);

        ruleResult.setRuleId(msEntryRule.getRuleId());
        ruleResult.setTransactionId(ruleExecution.getTransactionId());

        ruleResult.setExecutionStatus(ruleExecution.getExecutionStatus());

        return ruleResult;

    }

    private <T extends Rule> RuleResult executeKmehrMessageRule(KmehrMessageRule kmehrMessageRule, Kmehrmessage kmehrmessage) {

        RuleExecution ruleExecution = executeRule(kmehrMessageRule, kmehrmessage);

        String ruleExecutionMessage = ruleExecution.getMessage();
        if (kmehrMessageRule instanceof CustomMessage) {
            CustomMessage customMessage = (CustomMessage) kmehrMessageRule;
            ruleExecutionMessage = ruleExecutionMessage + customMessage.getCustomMessage();
        }
        RuleResult ruleResult = new RuleResult(kmehrMessageRule.getLevel(), ruleExecutionMessage);

        ruleResult.setRuleId(kmehrMessageRule.getRuleId());

        ruleResult.setExecutionStatus(ruleExecution.getExecutionStatus());

        return ruleResult;

    }

    private <T extends Rule> RuleResult executeKmehrMessageStringRule(KmehrMessageStringRule kmehrMessageStringRule, String kmehrmessageString) {

        RuleExecution ruleExecution = executeRule(kmehrMessageStringRule, kmehrmessageString);

        String ruleExecutionMessage = ruleExecution.getMessage();
        if (kmehrMessageStringRule instanceof CustomMessage) {
            CustomMessage customMessage = (CustomMessage) kmehrMessageStringRule;
            ruleExecutionMessage = ruleExecutionMessage + customMessage.getCustomMessage();
        }
        RuleResult ruleResult = new RuleResult(kmehrMessageStringRule.getLevel(), ruleExecutionMessage);

        ruleResult.setRuleId(kmehrMessageStringRule.getRuleId());

        ruleResult.setExecutionStatus(ruleExecution.getExecutionStatus());

        return ruleResult;

    }

    private RuleExecution executeRule(KmehrMessageRule kmehrMessageRule, Kmehrmessage kmehrmessage) {
        try {
            return kmehrMessageRule.performValidation(kmehrmessage);
        } catch (Throwable t) {
            return new RuleExecution(ExecutionStatus.INTERRUPTED, ExceptionUtils.getFullStackTrace(t).replace(System.lineSeparator(), " "));
        }
    }

    private RuleExecution executeRule(KmehrMessageStringRule kmehrMessageStringRule, String kmehrMessageString) {
        try {
            return kmehrMessageStringRule.performValidation(kmehrMessageString);
        } catch (Throwable t) {
            return new RuleExecution(ExecutionStatus.INTERRUPTED, ExceptionUtils.getFullStackTrace(t).replace(System.lineSeparator(), " "));
        }
    }

    private RuleExecution executeRule(MSEntryRule msEntryRule, MSEntry msEntry) {
        try {
            return msEntryRule.performValidation(msEntry);
        } catch (Throwable t) {
            return new RuleExecution(ExecutionStatus.INTERRUPTED, ExceptionUtils.getFullStackTrace(t).replace(System.lineSeparator(), " "));
        }
    }

    public void setSkipDisabledRules(boolean skipDisabledRules) {
        this.skipDisabledRules = skipDisabledRules;
    }
}
