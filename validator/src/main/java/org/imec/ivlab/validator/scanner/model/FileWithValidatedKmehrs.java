package org.imec.ivlab.validator.scanner.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileWithValidatedKmehrs {

    private File file;

    private List<ValidatedKmehr> validatedKmehrs = new ArrayList<>();

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<ValidatedKmehr> getValidatedKmehrs() {
        return validatedKmehrs;
    }

    public void setValidatedKmehrs(List<ValidatedKmehr> validatedKmehrs) {
        this.validatedKmehrs = validatedKmehrs;
    }

}
