package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDPERIODICITY;
import be.fgov.ehealth.standards.kmehr.schema.v1.FrequencyType;

public class FrequencyUtil {

    public static CDPERIODICITY getDayPeriod(FrequencyType frequencyType) {


        if (frequencyType == null || frequencyType.getPeriodicity() == null) {
            return null;
        }

        return frequencyType.getPeriodicity().getCd();

    }


}
