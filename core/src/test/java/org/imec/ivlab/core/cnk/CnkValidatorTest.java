package org.imec.ivlab.core.cnk;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CnkValidatorTest {

    @Test
    public void testIsValidCnkForValidCnk() throws Exception {
        Assert.assertTrue(CnkValidator.isValidCnk("2642619"));
        Assert.assertTrue(CnkValidator.isValidCnk("0056135"));
        Assert.assertTrue(CnkValidator.isValidCnk("37556"));
        Assert.assertTrue(CnkValidator.isValidCnk("83451"));
        Assert.assertTrue(CnkValidator.isValidCnk("1142488"));
        Assert.assertTrue(CnkValidator.isValidCnk("2585800"));
        Assert.assertTrue(CnkValidator.isValidCnk("2981082"));
        Assert.assertTrue(CnkValidator.isValidCnk("096917"));
        Assert.assertTrue(CnkValidator.isValidCnk("1459064"));
        Assert.assertTrue(CnkValidator.isValidCnk("2396083"));
        Assert.assertTrue(CnkValidator.isValidCnk("2329969"));
        Assert.assertTrue(CnkValidator.isValidCnk("1365543"));
        Assert.assertTrue(CnkValidator.isValidCnk("2621936"));
        Assert.assertTrue(CnkValidator.isValidCnk("1690262"));
        Assert.assertTrue(CnkValidator.isValidCnk("3040532"));
        Assert.assertTrue(CnkValidator.isValidCnk("1439561"));
    }

    @Test
    public void testIsValidCnkForInvalidValidCnk() throws Exception {
        Assert.assertFalse(CnkValidator.isValidCnk("26426199"));
        Assert.assertFalse(CnkValidator.isValidCnk("2642618"));
        Assert.assertFalse(CnkValidator.isValidCnk("0056136"));
        Assert.assertFalse(CnkValidator.isValidCnk("0032556"));
        Assert.assertFalse(CnkValidator.isValidCnk("0083471"));
        Assert.assertFalse(CnkValidator.isValidCnk("1132488"));
        Assert.assertFalse(CnkValidator.isValidCnk("2515800"));
        Assert.assertFalse(CnkValidator.isValidCnk("2986082"));
        Assert.assertFalse(CnkValidator.isValidCnk("0093917"));
        Assert.assertFalse(CnkValidator.isValidCnk("1451064"));
        Assert.assertFalse(CnkValidator.isValidCnk("2396483"));
        Assert.assertFalse(CnkValidator.isValidCnk("2329769"));
        Assert.assertFalse(CnkValidator.isValidCnk("2366543"));
        Assert.assertFalse(CnkValidator.isValidCnk("2621937"));
        Assert.assertFalse(CnkValidator.isValidCnk("1690232"));
        Assert.assertFalse(CnkValidator.isValidCnk("3040432"));
    }

    @Test
    public void testCodeReservedForLocalUsage() {

        Assert.assertFalse(CnkValidator.isReservedForLocalUsage("0090001"));
        Assert.assertFalse(CnkValidator.isReservedForLocalUsage("0098962"));

        Assert.assertTrue(CnkValidator.isReservedForLocalUsage("0900000"));
        Assert.assertTrue(CnkValidator.isReservedForLocalUsage("0900001"));
        Assert.assertTrue(CnkValidator.isReservedForLocalUsage("0999999"));

    }

}