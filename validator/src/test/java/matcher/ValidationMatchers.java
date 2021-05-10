package matcher;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.KmehrMessageRule;
import org.imec.ivlab.validator.validators.business.rules.Rule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleResult;
import org.imec.ivlab.validator.validators.model.AbstractValidationItem;
import org.imec.ivlab.validator.validators.model.ValidationResult;

import java.util.List;
import java.util.Objects;

public class ValidationMatchers {

    public static <T extends Rule> Matcher<ValidationResult> failsForRule(Class<T> ruleClass) {

            T rule;
            try {
                 rule = ruleClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to istantiate rule.", e);
            }

            return new BaseMatcher<ValidationResult>() {


                @Override
                public boolean matches(final Object item) {

                    final ValidationResult validationResult = (ValidationResult) item;

                    List<AbstractValidationItem> failedList = validationResult.getFailedList();

                    for (AbstractValidationItem validationItem : failedList) {
                        if (validationItem.getClass().isAssignableFrom(RuleResult.class)) {
                            RuleResult ruleResult = (RuleResult) validationItem;

                            if (Objects.equals(rule.getRuleId(), ruleResult.getRuleId())) {
                                return true;
                            }
                        }
                    }

                    return false;

                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText("validationresult should have failed for rule ").appendValue(rule.getRuleId());
                }

            };


    }

    public static <T extends Rule> Matcher<ValidationResult> failsWithMessage(String containsMessageIgnoringCase) {

        return new BaseMatcher<ValidationResult>() {


            @Override
            public boolean matches(final Object item) {

                final ValidationResult validationResult = (ValidationResult) item;

                List<AbstractValidationItem> failedList = validationResult.getFailedList();

                for (AbstractValidationItem validationItem : failedList) {
                    if (StringUtils.containsIgnoreCase(validationItem.getMessage(), containsMessageIgnoringCase)) {
                        return true;
                    }
                }

                return false;

            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("validationresult should have failed with message " + containsMessageIgnoringCase);
            }

        };


    }

    public static <T extends KmehrMessageRule> Matcher<ValidationResult> failsForAnyRule() {


        return new BaseMatcher<ValidationResult>() {


            @Override
            public boolean matches(final Object item) {

                final ValidationResult validationResult = (ValidationResult) item;

                return (CollectionsUtil.size(validationResult.getFailedList()) + CollectionUtils.size(validationResult.getInterruptedList())) > 0;

            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("validationresult should have failed for at least one rule");
            }

        };


    }

    /**
     * The ruleClass is not taken into account, we check that the message passed all rules. The class is passed as a reference to track relationship between tests and rules
     * @param ruleClass
     * @param <T>
     * @return
     */
    public static <T extends Rule> Matcher<ValidationResult> passes(Class<T> ruleClass) {

        return passes();


    }

    public static <T extends Rule> Matcher<ValidationResult> passes() {

        return new BaseMatcher<ValidationResult>() {

            ValidationResult validationResult;

            @Override
            public boolean matches(final Object item) {

                validationResult = (ValidationResult) item;

                return CollectionUtils.isEmpty(validationResult.getFailedList()) && CollectionUtils.isEmpty(validationResult.getInterruptedList());

            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("validationresult should have passed all rules. Failing rule messages: " + mergeMessages(validationResult));
            }

            private String mergeMessages(ValidationResult validationResult) {

                if (validationResult != null) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (AbstractValidationItem abstractValidationItem : validationResult.getFailedList()) {
                        stringBuffer.append(abstractValidationItem.getMessage() + System.lineSeparator());
                    }
                    for (AbstractValidationItem abstractValidationItem : validationResult.getInterruptedList()) {
                        stringBuffer.append(abstractValidationItem.getMessage() + System.lineSeparator());
                    }

                    return stringBuffer.toString();

                }

                return null;

            }

        };


    }


}
