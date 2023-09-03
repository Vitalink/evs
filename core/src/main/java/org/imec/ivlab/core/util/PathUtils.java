package org.imec.ivlab.core.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {

    private final static Logger log = LogManager.getLogger(PathUtils.class);


    public static String relativizePath(String firstPath, String secondPath) {

        if (new File(secondPath).isAbsolute()) {
            log.trace("path is absolute: " + secondPath + ". Will calculate relative path to: " + firstPath);
            Path pathOne = Paths.get(firstPath);
            Path pathTwo = new File(secondPath).toPath();
            Path pathTwoRelativeToPathOne = pathOne.relativize(pathTwo);
            log.trace("calculated relative path to: " + firstPath + " is: " + pathTwoRelativeToPathOne.toString());
            return pathTwoRelativeToPathOne.toString();
        } else {
            log.trace("path is already relative: " + secondPath);
            return secondPath;
        }

    }

    public static String commonParent(String[] files) {

        if (files == null || files.length == 0) {
            return null;
        }

        log.debug("Calculating common parent for files:");

        String[] paths = new String[files.length];
        int shortestPathLength = -1;
        for (int i = 0; i < files.length; i++) {
            String filePath = files[i];
            filePath = OSUtils.adaptFilePath(filePath, File.separator);
            log.debug(filePath);
            File file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("File doesn't exist: " + file.getAbsolutePath());
            }
            if (file.isFile()) {
                filePath = file.getParent();
            }
            if (!org.apache.commons.lang.StringUtils.endsWith(filePath, File.separator)) {
                filePath = filePath + File.separator;
            }
            if (shortestPathLength == -1 || filePath.length() < shortestPathLength) {
                shortestPathLength = filePath.length();
            }
            paths[i] = filePath;
        }

        int charPosition = 0;
        outerloop:
        while (charPosition < shortestPathLength) {
            Character charPosFirstPath = paths[0].charAt(charPosition);
            for (int i = 0; i < paths.length; i++) {
                char charAtPosition = paths[i].charAt(charPosition);
                if (charAtPosition != charPosFirstPath) {
                    break outerloop;
                }
            }
            charPosition++;
        }

        String commonString = org.apache.commons.lang.StringUtils.left(paths[0], charPosition);
        log.debug("Common string for all paths: " + commonString);
        return commonString;


    }


}
