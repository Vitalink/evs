package org.imec.ivlab.datagenerator.uploader.exception;

public class InvalidFolderStructureException extends Exception {

    public InvalidFolderStructureException(String message) {
        super(message);
    }

    public InvalidFolderStructureException(String message, Throwable e) {
        super(message, e);
    }

    public InvalidFolderStructureException(Throwable e) {
        super(e);
    }

}
