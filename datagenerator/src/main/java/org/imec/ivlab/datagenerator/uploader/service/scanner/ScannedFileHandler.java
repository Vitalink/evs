package org.imec.ivlab.datagenerator.uploader.service.scanner;

import java.io.File;

public interface ScannedFileHandler {

    void process(File rootFolder, File file);
}
