package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.schema.v1.HeaderType;

public class KmehrHeaderUtil {

    public static String getStandard(HeaderType headerType) {

        if (headerType == null || headerType.getStandard() == null || headerType.getStandard() == null || headerType.getStandard().getCd() == null) {
            return null;
        }

        return headerType.getStandard().getCd().getValue();

    }

}
