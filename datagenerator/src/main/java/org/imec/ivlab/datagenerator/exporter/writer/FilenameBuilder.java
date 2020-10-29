package org.imec.ivlab.datagenerator.exporter.writer;

import org.apache.commons.lang3.StringUtils;

public class FilenameBuilder {

    private StringBuffer stringBuffer = new StringBuffer();

    private static final String separator = "_";

    public FilenameBuilder add(String filenamePart) {
        if (filenamePart != null) {
            stringBuffer.append(filenamePart);
            stringBuffer.append(separator);
        }
        return this;
    }

    public String generateString() {
        String generatedString = stringBuffer.toString();
        return StringUtils.removeEnd(generatedString, separator);
    }

    @Override
    public String toString() {
        return generateString();
    }
}
