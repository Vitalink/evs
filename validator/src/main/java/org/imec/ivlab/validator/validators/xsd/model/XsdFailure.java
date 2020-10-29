package org.imec.ivlab.validator.validators.xsd.model;

import org.imec.ivlab.validator.validators.model.AbstractValidationItem;
import org.imec.ivlab.validator.validators.model.Level;

public class XsdFailure extends AbstractValidationItem {

    public XsdFailure(Level level, String message) {
        super(level, message);
    }

}
