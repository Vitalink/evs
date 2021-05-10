package org.imec.ivlab.core.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class OSUtils {

    public static String adaptFilePath(String filePath) {

        return adaptFilePath(filePath, File.separator);

    }

    public static String adaptFilePath(String filePath, String fileSeparator) {

        String cleanedPath = filePath;

        cleanedPath = StringUtils.replaceChars(cleanedPath, "/", fileSeparator);
        cleanedPath = StringUtils.replaceChars(cleanedPath, "\\", fileSeparator);

        return cleanedPath;

    }

}
