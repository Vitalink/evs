package org.imec.ivlab.core.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

@Test
public class NumberUtilTest {

    @Test
    public void testIsZero() throws Exception {
        Assert.assertTrue(NumberUtil.isZero(new BigDecimal("0")));
        Assert.assertTrue(NumberUtil.isZero(new BigDecimal("0.0")));
        Assert.assertTrue(NumberUtil.isZero(new BigDecimal("0.0000")));
        Assert.assertTrue(NumberUtil.isZero(BigDecimal.ZERO));
        Assert.assertFalse(NumberUtil.isZero(new BigDecimal("0.0001")));
        Assert.assertFalse(NumberUtil.isZero(new BigDecimal("5")));
    }


    @Test
    public void testIsIntegerNumber() throws Exception {

        Assert.assertTrue(NumberUtil.isInteger(new BigDecimal("5")));
        Assert.assertTrue(NumberUtil.isInteger(new BigDecimal("0")));
        Assert.assertTrue(NumberUtil.isInteger(new BigDecimal("-1")));
        Assert.assertTrue(NumberUtil.isInteger(new BigDecimal("-99")));
        Assert.assertFalse(NumberUtil.isInteger(new BigDecimal("5.000")));
        Assert.assertFalse(NumberUtil.isInteger(new BigDecimal("5.99")));
        Assert.assertFalse(NumberUtil.isInteger(new BigDecimal("0.00")));

    }

    @Test
    public void testGetIntegerDigitCount() throws Exception {

        Assert.assertEquals(NumberUtil.getIntegerDigitCount(new BigDecimal("5")), 1);
        Assert.assertEquals(NumberUtil.getIntegerDigitCount(new BigDecimal("5.0")), 1);
        Assert.assertEquals(NumberUtil.getIntegerDigitCount(new BigDecimal("55")), 2);
        Assert.assertEquals(NumberUtil.getIntegerDigitCount(new BigDecimal("555")), 3);
        Assert.assertEquals(NumberUtil.getIntegerDigitCount(new BigDecimal("0")), 1);
        Assert.assertEquals(NumberUtil.getIntegerDigitCount(new BigDecimal("0.00")), 1);
        Assert.assertEquals(NumberUtil.getIntegerDigitCount(new BigDecimal("0.99")), 1);
        Assert.assertEquals(NumberUtil.getIntegerDigitCount(new BigDecimal("0.09")), 1);
        Assert.assertEquals(NumberUtil.getIntegerDigitCount(new BigDecimal("-0.09")), 1);
        Assert.assertEquals(NumberUtil.getIntegerDigitCount(new BigDecimal("1.09")), 1);
        Assert.assertEquals(NumberUtil.getIntegerDigitCount(new BigDecimal("-1.09")), 1);
        Assert.assertEquals(NumberUtil.getIntegerDigitCount(new BigDecimal("10.09")), 2);

    }

    @Test
    public void testGetFractionalDigitCount() throws Exception {

        Assert.assertEquals(NumberUtil.getFractionalDigitCount(new BigDecimal("5")), 0);
        Assert.assertEquals(NumberUtil.getFractionalDigitCount(new BigDecimal("5.0")), 1);
        Assert.assertEquals(NumberUtil.getFractionalDigitCount(new BigDecimal("5.0001")), 4);
        Assert.assertEquals(NumberUtil.getFractionalDigitCount(new BigDecimal("55")), 0);
        Assert.assertEquals(NumberUtil.getFractionalDigitCount(new BigDecimal("0")), 0);
        Assert.assertEquals(NumberUtil.getFractionalDigitCount(new BigDecimal("0.00")), 2);
        Assert.assertEquals(NumberUtil.getFractionalDigitCount(new BigDecimal("3.31000")), 5);

    }
    @Test
    public void testGetTrailingZeroesCount() throws Exception {

        Assert.assertTrue(NumberUtil.hasTrailingZeroes(new BigDecimal("5.0")));
        Assert.assertTrue(NumberUtil.hasTrailingZeroes(new BigDecimal("0.00")));
        Assert.assertTrue(NumberUtil.hasTrailingZeroes(new BigDecimal("3.10000")));
        Assert.assertTrue(NumberUtil.hasTrailingZeroes(new BigDecimal("3.10")));
        Assert.assertFalse(NumberUtil.hasTrailingZeroes(new BigDecimal("5")));
        Assert.assertFalse(NumberUtil.hasTrailingZeroes(new BigDecimal("5.0001")));
        Assert.assertFalse(NumberUtil.hasTrailingZeroes(new BigDecimal("55")));
        Assert.assertFalse(NumberUtil.hasTrailingZeroes(new BigDecimal("0")));

    }

}