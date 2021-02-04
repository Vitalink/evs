package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class TextTypeUtil {

    public static List<String> toStrings(List<TextType> textTypes) {

        return Optional
            .ofNullable(textTypes)
            .orElse(Collections.emptyList())
            .stream()
            .map(TextType::getValue)
            .map(StringUtils::trimToNull)
            .collect(Collectors.toList());

    }

}
