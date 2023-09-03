package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.apache.commons.collections.CollectionUtils;
import org.imec.ivlab.validator.validators.business.rules.BaseKmehrMessageRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

public class R1100_OneFolder extends BaseKmehrMessageRule {

    @Override
    public String getMessage() {
        return "There should be exact 1 folder";
    }

    @Override
    public String getRuleId() {
        return "1100";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(Kmehrmessage kmehrmessage)  {
        if (CollectionUtils.size(kmehrmessage.getFolders()) == 1) {
            return passRule();
        } else {
            return failRule();
        }

    }


}
