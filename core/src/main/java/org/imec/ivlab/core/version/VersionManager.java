package org.imec.ivlab.core.version;

import org.apache.log4j.Logger;
import org.imec.ivlab.core.exceptions.LocalVersionCheckFailedException;
import org.imec.ivlab.core.exceptions.RemoteVersionCheckFailedException;
import org.imec.ivlab.core.util.ConsoleUtils;

public class VersionManager {

    private final static Logger LOG = Logger.getLogger(VersionManager.class);

    private static VersionCheckResult versionCheckResult;

    public static VersionCheckResult getVersionCheckResult() {

        if (versionCheckResult == null) {
            versionCheckResult = checkForLatestVersion();
        }

        return versionCheckResult;
    }

    public static void printUpdateMessage() {

        VersionCheckResult versionCheckResult;
        try {
            versionCheckResult = getVersionCheckResult();
        } catch (Exception e) {
            LOG.error(e);
            return;
        }

        if (!versionCheckResult.isUpToDate()) {

            LOG.info(System.lineSeparator() + ConsoleUtils.emphasizeTitle("A newer version is available: " + versionCheckResult.getRemoteVersion()));

        }


    }


    private static VersionCheckResult checkForLatestVersion() throws LocalVersionCheckFailedException, RemoteVersionCheckFailedException {

        String installedVersionNumber = LocalVersionReader.getInstalledVersionNumber();
        String remoteVersion = RemoteVersionReader.getRemoteVersion();

        boolean upToDate = VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo(installedVersionNumber, remoteVersion);

        VersionCheckResult result = new VersionCheckResult();
        result.setLocalVersion(installedVersionNumber);
        result.setRemoteVersion(remoteVersion);
        result.setUpToDate(upToDate);
        return result;

    }



}
