package org.imec.ivlab.validator.validators;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.kmehr.KmehrMarshaller;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.scanner.model.FileWithKmehrs;
import org.imec.ivlab.validator.scanner.model.FileWithValidatedKmehrs;
import org.imec.ivlab.validator.scanner.model.ValidatedKmehr;
import org.imec.ivlab.validator.validators.business.BusinessValidator;
import org.imec.ivlab.validator.validators.business.model.BusinessValidationResult;
import org.imec.ivlab.validator.validators.business.rules.model.RuleResult;
import org.imec.ivlab.validator.validators.model.AbstractValidationItem;
import org.imec.ivlab.validator.validators.model.ValidationResult;
import org.imec.ivlab.validator.validators.xsd.XsdValidator;
import org.imec.ivlab.validator.validators.xsd.handler.XsdValidationResult;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

public class KmehrValidator {

    private static Logger LOG = LogManager.getLogger(KmehrValidator.class);

    private static XsdValidator xsdValidator = new XsdValidator();
    private static BusinessValidator businessValidator = new BusinessValidator();

    public void setXsdValidator(XsdValidator xsdValidator) {
        KmehrValidator.xsdValidator = xsdValidator;
    }

    public void setBusinessValidator(BusinessValidator businessValidator) {
        KmehrValidator.businessValidator = businessValidator;
    }

    public List<FileWithValidatedKmehrs> performValidation(List<FileWithKmehrs> filesWithKmehrs) {

        ArrayList<FileWithValidatedKmehrs> filesWithValidatedKmehrs = new ArrayList<>();

        KmehrValidator kmehrValidator = new KmehrValidator();

        for (FileWithKmehrs fileWithKmehrs : filesWithKmehrs) {

            FileWithValidatedKmehrs fileWithValidatedKmehrs = new FileWithValidatedKmehrs();
            fileWithValidatedKmehrs.setFile(fileWithKmehrs.getFile());

            ValidationResult validationResult = null;
            if (fileWithKmehrs.getKmehrmessage() != null) {

                String kmehrmessageString;
                try {
                    kmehrmessageString = KmehrMarshaller.toString(fileWithKmehrs.getKmehrmessage());
                } catch (JAXBException e) {
                    throw new RuntimeException("Failed to write kmehr message to string", e);
                }

                validationResult = kmehrValidator.validate(fileWithKmehrs.getKmehrmessage(), kmehrmessageString);
            }

            fileWithValidatedKmehrs.getValidatedKmehrs().add(new ValidatedKmehr(fileWithKmehrs.getKmehrmessage(), validationResult));

            filesWithValidatedKmehrs.add(fileWithValidatedKmehrs);

        }

        return filesWithValidatedKmehrs;

    }


    protected ValidationResult validate(Kmehrmessage kmehrmessage, String kmehrmessageString) {

        ValidationResult result = new ValidationResult();

        XsdValidationResult xsdValidationResult = xsdValidator.validate(kmehrmessage);

        result.getFailedList().addAll(xsdValidationResult.getFailedList());

        BusinessValidationResult businessValidationResult = businessValidator.validate(kmehrmessage, kmehrmessageString);

        result.getPassedList().addAll(businessValidationResult.getPassedList());
        result.getFailedList().addAll(businessValidationResult.getFailedList());
        result.getInterruptedList().addAll(businessValidationResult.getInterruptedList());


        LOG.info("Rules passed: " + result.getPassedRulesCount() + ". Failed: " + result.getFailedRulesCount() + ". Interrupted: " + result.getInterruptedRulesCount());
        if (result.getFailedList().size() > 0) {
            LOG.info("Failing rules: " + System.lineSeparator() + printAbstractItems(result.getFailedList()));
        }
        if (result.getInterruptedList().size() > 0) {
            LOG.info("Interrupted rules: "  + System.lineSeparator() + printRuleResults(result.getInterruptedList()));
        }

        return result;

    }


    protected String printRuleResults(List<RuleResult> ruleResults) {
        if (CollectionsUtil.emptyOrNull(ruleResults)) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (RuleResult ruleResult : ruleResults) {
            sb.append("* " + ruleResult.getRuleId() + " - " + ruleResult.getMessage() + System.lineSeparator());
        }

        return sb.toString();
    }

    protected String printAbstractItems(List<AbstractValidationItem> ruleResults) {

        if (CollectionsUtil.emptyOrNull(ruleResults)) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (AbstractValidationItem abstractValidationItem : ruleResults) {
            if (abstractValidationItem instanceof RuleResult) {
                RuleResult ruleResult = (RuleResult) abstractValidationItem;
                sb.append("* " + ruleResult.getRuleId() + " - " + ruleResult.getMessage() + System.lineSeparator());
            } else {
                sb.append("* " + abstractValidationItem.getMessage() + System.lineSeparator());
            }
        }

        return sb.toString();
    }

    public void skipDisabledRules(boolean skipDisabledRules) {
        businessValidator.setSkipDisabledRules(skipDisabledRules);
    }

}
