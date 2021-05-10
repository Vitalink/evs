package org.imec.ivlab.core.model.patient.exceptions;

public class PatientDataNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public PatientDataNotFoundException(String message) {
        super(message);
    }

    public PatientDataNotFoundException(String message, Throwable e) {
        super(message, e);
    }

}
