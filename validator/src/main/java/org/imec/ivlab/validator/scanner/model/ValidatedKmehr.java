package org.imec.ivlab.validator.scanner.model;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.imec.ivlab.validator.validators.model.ValidationResult;

public class ValidatedKmehr {

    private Kmehrmessage kmehrmessage;
    private ValidationResult validationResult;

    public Kmehrmessage getContent() {
        return kmehrmessage;
    }

    public void setContent(Kmehrmessage content) {
        this.kmehrmessage = content;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public ValidatedKmehr(Kmehrmessage content, ValidationResult validationResult) {
        this.kmehrmessage = content;
        this.validationResult = validationResult;
    }
}
