package org.imec.ivlab.core.version;

import org.testng.Assert;
import org.testng.annotations.Test;

public class VersionCompareTest {

    @Test
    public void testIsVersionOneHigherThanOrEqualsVersionTwo() throws Exception {

        Assert.assertTrue(VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo("4.0", "4.0"));
        Assert.assertTrue(VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo("4.1", "4.0"));
        Assert.assertTrue(VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo("4.1.1", "4.0.1"));
        Assert.assertTrue(VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo("4.2.3", "4.2.3"));
        Assert.assertTrue(VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo("1.2.1.1", "1.1"));

        Assert.assertFalse(VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo("4.2.3", "4.2.4"));
        Assert.assertFalse(VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo("4.2.3", "4.3"));
        Assert.assertFalse(VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo("4.2.3", "4.3.0"));


        Assert.assertFalse(VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo("3", "4.0.0"));
        Assert.assertFalse(VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo("3.0.0", "4"));

        Assert.assertFalse(VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo("4.2.3", "4.3.0-SNAPSHOT"));
        Assert.assertFalse(VersionComparator.isVersionOneHigherThanOrEqualToVersionTwo("4.2.3-SNAPSHOT", "4.3.0"));

    }

}