package org.imec.ivlab.core.version;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.constants.CoreConstants;
import org.imec.ivlab.core.exceptions.LocalVersionCheckFailedException;
import org.imec.ivlab.core.exceptions.ResourceException;
import org.imec.ivlab.core.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.Properties;

public class LocalVersionReader {

    private final static Logger log = LogManager.getLogger(LocalVersionReader.class);

    private static String versionNumber = null;

    public static String getInstalledSoftwareAndVersion() throws LocalVersionCheckFailedException {
        if (versionNumber == null) {
            try {
                versionNumber = getInstalledVersionNumber();
            } catch (LocalVersionCheckFailedException e) {
                return CoreConstants.EVS_NAME;
            }
        }
        return CoreConstants.EVS_NAME + " " + versionNumber;
    }


    public static String getInstalledVersionNumber() throws LocalVersionCheckFailedException {

    Properties properties = new Properties();
    try {
        String classLocation = LocalVersionReader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String classLocationParent = new File(classLocation).getParent();
        String path = Paths.get(URLDecoder.decode(classLocationParent, "UTF-8")).toFile() + File.separator + "version.txt";

        properties.load(IOUtils.getResourceAsStream(path));
        if (properties.containsKey("version")) {
            String version = (String) properties.get("version");
            if (!StringUtils.isEmpty(version)) {
                return version;
            } else {
                throw new LocalVersionCheckFailedException("The local version number is empty.");
            }
        } else {
            throw new LocalVersionCheckFailedException("Failed to get software version");
        }
    } catch (IOException | ResourceException e) {
        throw new LocalVersionCheckFailedException("Failed to get software version: " + e);
    }
    }


}
