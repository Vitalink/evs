package org.imec.ivlab.core.util;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.length;

public class FilenameUtil {

    private final static int MAX_FILENAME_SIZE = 250;

    private final static String FILENAME_CUT_MESSAGE = "_NAME_TOO_LONG";

    public static File chompIfTooLong(File file) {

        if (file == null) {
            return null;
        }

        String fileName = FilenameUtils.getName(file.getName());

        if (length(fileName) > MAX_FILENAME_SIZE) {

            String extension = FilenameUtils.getExtension(fileName);
            String filenameWithoutExtension = FilenameUtils.removeExtension(fileName);

            String filenameChomped = left(filenameWithoutExtension, MAX_FILENAME_SIZE - (length(extension) + 1) - length(FILENAME_CUT_MESSAGE));

            return new File(file.getParent() + File.separator + filenameChomped + FILENAME_CUT_MESSAGE + "." + extension);

        } else {
            return file;
        }

    }

}
