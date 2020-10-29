package org.imec.ivlab.validator.validators.business.rules;

import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;

public interface MSEntryRule extends Rule {

    RuleExecution performValidation(MSEntry msEntry);

}
