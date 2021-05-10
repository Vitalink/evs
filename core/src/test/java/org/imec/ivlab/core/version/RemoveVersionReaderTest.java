package org.imec.ivlab.core.version;

import org.testng.annotations.Test;

public class RemoveVersionReaderTest {

    @Test
    public void testGetRemoteVersion() throws Exception {
        RemoteVersionReader removeVersionReader = new RemoteVersionReader();
        String remoteVersion = removeVersionReader.getRemoteVersion();
        System.out.print(remoteVersion);
    }

}