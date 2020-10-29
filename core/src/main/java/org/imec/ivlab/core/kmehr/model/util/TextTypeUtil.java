package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;

import java.util.ArrayList;
import java.util.List;

public class TextTypeUtil {

    public static List<String> toStrings(List<TextType> textTypes) {

        if (textTypes == null) {
            return null;
        }

        List<String> strings = new ArrayList<>();

        for (TextType textType : textTypes) {
            if (textType != null) {
                strings.add(textType.getValue());
            }
        }

        return strings;

    }

}
