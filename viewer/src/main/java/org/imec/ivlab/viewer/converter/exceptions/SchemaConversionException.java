package org.imec.ivlab.viewer.converter.exceptions;

public class SchemaConversionException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public SchemaConversionException(String message) {
        super(message);
    }

    public SchemaConversionException(String message, Throwable e) {
        super(message, e);
    }

}
