package org.imec.ivlab.validator.validators.business.rules;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;

public interface KmehrMessageRule extends Rule {

    RuleExecution performValidation(Kmehrmessage Kmehrmessage);

}
