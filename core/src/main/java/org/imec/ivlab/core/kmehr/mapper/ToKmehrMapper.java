package org.imec.ivlab.core.kmehr.mapper;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDSEXvalues;
import org.imec.ivlab.core.model.patient.model.Gender;

public class ToKmehrMapper {

    public static CDSEXvalues genderToCDSEXValues(Gender gender) {
        switch (gender) {

            case MALE:
                return CDSEXvalues.MALE;
            case FEMALE:
                return CDSEXvalues.FEMALE;
            default:
                return CDSEXvalues.UNKNOWN;

        }
    }

}
