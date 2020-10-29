package org.imec.ivlab.core.cnk;

import org.apache.commons.lang3.StringUtils;

public class CnkValidator {

    private static final Integer LOCAL_USAGE_LOWER_BOUNDARY = 900000;
    private static final Integer LOCAL_USAGE_UPPER_BOUNDARY = 999999;

    public static boolean isReservedForLocalUsage(String cnkString) {

        if (cnkString == null) {
            return false;
        }

        Integer cnkNumber = Integer.parseInt(cnkString);

        if (cnkNumber >= LOCAL_USAGE_LOWER_BOUNDARY && cnkNumber <= LOCAL_USAGE_UPPER_BOUNDARY) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isValidCnk(String cnkString) {

        if (!hasValidLength(cnkString)) {
            return false;
        }

        return hasValidControlNumber(cnkString);

    }

    private static boolean hasValidLength(String cnkString) {
        return StringUtils.length(cnkString) <= 7;
    }

    private static boolean hasValidControlNumber(String cnkString) {

        cnkString = StringUtils.leftPad(cnkString, 7, '0');

        int sum = 0;
        for (int charPos = 0; charPos < 6; charPos++) {
            sum += getControlPartialValue(charPos, cnkString);
        }

        int controlNumber = Character.getNumericValue(cnkString.charAt(6));

        return (controlNumber + sum == roundToNextTiental(sum));

    }

    private static int roundToNextTiental(int number) {

        int remainder = number % 10;

        if (remainder > 0) {
            return (number - remainder) + 10;
        } else {
            return number;
        }

    }

    private static int getControlPartialValue(int charPosition, String cnkString) {

        int numericValue = Character.getNumericValue(cnkString.charAt(charPosition));

        if (charPosition % 2 == 1) {
            numericValue *= 2;
        }

        if (numericValue >= 10) {
            return 1 + (numericValue - 10);
        } else {
            return numericValue;
        }

    }



}
