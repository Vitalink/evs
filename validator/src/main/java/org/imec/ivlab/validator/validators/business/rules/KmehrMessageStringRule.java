package org.imec.ivlab.validator.validators.business.rules;

import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;

public interface KmehrMessageStringRule extends Rule {

    RuleExecution performValidation(String kmehrMessageString);

}
