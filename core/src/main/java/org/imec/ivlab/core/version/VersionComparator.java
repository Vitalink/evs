package org.imec.ivlab.core.version;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class VersionComparator {

    public static boolean isVersionOneHigherThanOrEqualToVersionTwo(String versionOneString, String versionTwoString) {

        DefaultArtifactVersion versionOne = new DefaultArtifactVersion(versionOneString);
        DefaultArtifactVersion versionTwo = new DefaultArtifactVersion(versionTwoString);

        if (versionOne.compareTo(versionTwo) >= 0) {
            return true;
        } else {
            return false;
        }

    }

}
