package org.imec.ivlab.core.model.patient.exceptions;

public class PatientDataConfigurationInvalidException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public PatientDataConfigurationInvalidException(String message) {
        super(message);
    }

    public PatientDataConfigurationInvalidException(String message, Throwable e) {
        super(message, e);
    }

}
