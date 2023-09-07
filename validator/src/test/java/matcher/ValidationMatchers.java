package matcher;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Condition;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.Rule;
//import org.imec.ivlab.validator.validators.business.rules.KmehrMessageRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleResult;
import org.imec.ivlab.validator.validators.model.AbstractValidationItem;
import org.imec.ivlab.validator.validators.model.ValidationResult;

public class ValidationMatchers {

    public static <T extends Rule> Condition<ValidationResult> failsForRule(Class<T> ruleClass) {

        T rule;
        try {
             rule = ruleClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to istantiate rule.", e);
        }

        return new Condition<>(validationResult -> {
            return validationResult.getFailedList().stream()
                    .filter(validationItem -> validationItem instanceof RuleResult)
                    .map(validationItem -> (RuleResult) validationItem)
                    .anyMatch(ruleResult -> ruleResult.getRuleId().equals(rule.getRuleId()));
        }, "validationresult should have failed for rule %s", rule.getRuleId());
    }

    public static Condition<ValidationResult> failsWithMessage(String containsMessageIgnoringCase) {
        return new Condition<>(validationResult -> {
            List<AbstractValidationItem> failedList = validationResult.getFailedList();
            for (AbstractValidationItem validationItem : failedList) {
                if (StringUtils.containsIgnoreCase(validationItem.getMessage(), containsMessageIgnoringCase)) {
                    return true;
                }
            }
            return false;
        }, "validationresult should have failed with message %s", containsMessageIgnoringCase);
    }

    public static Condition<ValidationResult> failsForAnyRule() {
        return new Condition<>(validationResult -> (CollectionsUtil.size(validationResult.getFailedList()) + CollectionUtils.size(validationResult.getInterruptedList())) > 0, "validationresult should have failed for at least one rule");
    }

    public static <T extends Rule> Condition<ValidationResult> passes(Class<T> ruleClass) {
        return passes();
    }

    public static Condition<ValidationResult> passes() {
        return new Condition<>(ValidationMatchers::validatePasses, "validationresult should have passed all rules. Failing rule messages: %s", "");
    }
    
    private static boolean validatePasses(ValidationResult validationResult) {
        return CollectionUtils.isEmpty(validationResult.getFailedList()) && CollectionUtils.isEmpty(validationResult.getInterruptedList());
    }

    public static String mergeMessages(ValidationResult validationResult) {
        if (validationResult != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (AbstractValidationItem abstractValidationItem : validationResult.getFailedList()) {
                stringBuilder.append(abstractValidationItem.getMessage()).append(System.lineSeparator());
            }
            for (AbstractValidationItem abstractValidationItem : validationResult.getInterruptedList()) {
                stringBuilder.append(abstractValidationItem.getMessage()).append(System.lineSeparator());
            }
            return stringBuilder.toString();
        }
        return null;
    }
}
