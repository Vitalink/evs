package org.imec.ivlab.validator.scanner.model;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;

import java.io.File;

public class FileWithKmehrs {

    private File file;

    private Kmehrmessage kmehrmessage;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Kmehrmessage getKmehrmessage() {
        return kmehrmessage;
    }

    public void setKmehrmessage(Kmehrmessage kmehrmessage) {
        this.kmehrmessage = kmehrmessage;
    }

    public FileWithKmehrs(File file, Kmehrmessage kmehrmessage) {
        this.file = file;
        this.kmehrmessage = kmehrmessage;
    }

}
