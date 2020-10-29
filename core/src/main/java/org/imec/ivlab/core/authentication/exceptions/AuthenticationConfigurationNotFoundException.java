package org.imec.ivlab.core.authentication.exceptions;

public class AuthenticationConfigurationNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public AuthenticationConfigurationNotFoundException(String message) {
        super(message);
    }

    public AuthenticationConfigurationNotFoundException(String message, Throwable e) {
        super(message, e);
    }

}
