package org.imec.ivlab.core.version;

import org.testng.annotations.Test;

public class RemoveVersionReaderTest {

    @Test
    public void testGetRemoteVersion() throws Exception {
        String remoteVersion = RemoteVersionReader.getRemoteVersion();
        System.out.print(remoteVersion);
    }

}