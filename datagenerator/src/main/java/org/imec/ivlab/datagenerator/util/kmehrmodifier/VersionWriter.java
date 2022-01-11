package org.imec.ivlab.datagenerator.util.kmehrmodifier;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.version.VersionCheckResult;

import java.io.File;

public class VersionWriter {

    public static final String VERSION_FILE_NAME_PART = "_versioninfo.txt";

    private final static Logger LOG = LogManager.getLogger(VersionWriter.class);

    public static boolean writeVersionInfoFileIfOutdated(File outputFile, VersionCheckResult versionCheckResult) {
        try {
            if (!versionCheckResult.isUpToDate()) {
                FileUtils.writeStringToFile(outputFile, "A newer EVS version exists: " + versionCheckResult.getRemoteVersion() + System.lineSeparator() +  "Your version: " + versionCheckResult.getLocalVersion(), "UTF-8", false);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
