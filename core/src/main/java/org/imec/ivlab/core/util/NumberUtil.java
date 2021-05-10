package org.imec.ivlab.core.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

    public static boolean isZero(BigDecimal number) {

        if (number == null) {
            throw new IllegalArgumentException("Provided number is null");
        }

        if (number.compareTo(BigDecimal.ZERO) == 0) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isInteger(BigDecimal number) {

        if (number == null) {
            throw new IllegalArgumentException("Provided number is null");
        }

        BigDecimal scaled = number.setScale(0, RoundingMode.FLOOR);

        if (number == scaled) {
            return true;
        } else {
            return false;
        }

    }

    public static int getIntegerDigitCount(BigDecimal number) {

        if (number == null) {
            throw new IllegalArgumentException("Provided number is null");
        }

        if (BigDecimal.ONE.compareTo(number) == 1 && BigDecimal.valueOf(-1L).compareTo(number) == -1) {
            return 1;
        } else {
            return number.precision() - number.scale();
        }

    }

    public static int getFractionalDigitCount(BigDecimal number) {

        if (number == null) {
            throw new IllegalArgumentException("Provided number is null");
        }

        return number.scale();

    }

    public static boolean hasTrailingZeroes(BigDecimal number) {

        if (number == null) {
            throw new IllegalArgumentException("Provided number is null");
        }

        if (isInteger(number)) {
            return false;
        }

        return (StringUtils.endsWith(number.toPlainString(), "0"));

    }

}
