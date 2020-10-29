package org.imec.ivlab.core.version;

public class VersionCheckResult {

    private boolean upToDate;
    private String remoteVersion;
    private String localVersion;

    public VersionCheckResult() {
    }

    public boolean isUpToDate() {
        return upToDate;
    }

    public void setUpToDate(boolean upToDate) {
        this.upToDate = upToDate;
    }

    public String getRemoteVersion() {
        return remoteVersion;
    }

    public void setRemoteVersion(String remoteVersion) {
        this.remoteVersion = remoteVersion;
    }

    public String getLocalVersion() {
        return localVersion;
    }

    public void setLocalVersion(String localVersion) {
        this.localVersion = localVersion;
    }
}
