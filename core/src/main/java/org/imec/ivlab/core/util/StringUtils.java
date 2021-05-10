package org.imec.ivlab.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static boolean contains(String wholeString, String regex, int flags) {

        Pattern pattern = Pattern.compile(regex, flags);

        Matcher matcher = pattern.matcher(wholeString);

        if (matcher.find()) {
            return true;
        }

        return false;

    }

    public static String joinFields(Object field1, Object field2) {
        return joinFields(field1, field2, System.lineSeparator() + System.lineSeparator());
    }

    public static String joinFields(Object field1, Object field2, String separator) {
        if (field1 != null) {
            if (field2 != null) {
                return org.apache.commons.lang3.StringUtils.joinWith(separator, field1, field2);
            } else {
                return field1.toString();
            }
        } else {
            if (field2 != null) {
                return field2.toString();
            } else {
                return null;
            }
        }
    }

    public static String joinWith(String separator, Object... fields) {

        if (fields == null) {
            return null;
        }

        int fieldIndex = 0;
        String joined = null;

        while (fieldIndex <= fields.length - 1) {

            joined = joinFields(joined, fields[fieldIndex], separator);
            fieldIndex++;

        }

        return joined;

    }

    public static String nullToString(String input) {
        if (input == null) {
            return "";
        }
        return input;
    }


}
