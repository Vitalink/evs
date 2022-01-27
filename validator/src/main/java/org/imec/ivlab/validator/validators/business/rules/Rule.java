package org.imec.ivlab.validator.validators.business.rules;

import org.imec.ivlab.validator.validators.model.Level;

public interface Rule {

    String getMessage();
    String getRuleId();
    Level getLevel();
    boolean enabled();


}
