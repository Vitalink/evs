package org.imec.ivlab.core.authentication.exceptions;

public class AuthenticationConfigurationErrorException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public AuthenticationConfigurationErrorException(String message) {
        super(message);
    }

    public AuthenticationConfigurationErrorException(String message, Throwable e) {
        super(message, e);
    }

}
