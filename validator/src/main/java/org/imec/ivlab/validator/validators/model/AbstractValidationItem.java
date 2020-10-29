package org.imec.ivlab.validator.validators.model;

public class AbstractValidationItem {

    public Level level;
    public String message;

    public AbstractValidationItem(Level level, String message) {
        this.level = level;
        this.message = message;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AbstractValidationItem{" +
                "level=" + level +
                ", message='" + message + '\'' +
                '}';
    }
}
