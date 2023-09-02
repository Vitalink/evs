package org.imec.ivlab.core.version;

import org.testng.annotations.Test;

public class RemoveVersionReaderTest {

    // Turned off as it fails now
    // Connect to wiki.ivlab.ilabt.imec.be:80 [wiki.ivlab.ilabt.imec.be/193.191.148.162] failed
    @Test(enabled = false)
    public void testGetRemoteVersion() throws Exception {
        String remoteVersion = RemoteVersionReader.getRemoteVersion();
        System.out.print(remoteVersion);
    }

}