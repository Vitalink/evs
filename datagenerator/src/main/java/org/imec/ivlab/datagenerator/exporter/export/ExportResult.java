package org.imec.ivlab.datagenerator.exporter.export;

import java.io.File;

public class ExportResult<T> {

    private T response;
    private File file;

    public ExportResult(T response, File file) {
        this.response = response;
        this.file = file;
    }

    public T getResponse() {
        return response;
    }

    public File getFile() {
        return file;
    }

}
