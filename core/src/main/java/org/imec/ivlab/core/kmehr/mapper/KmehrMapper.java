package org.imec.ivlab.core.kmehr.mapper;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTEMPORALITYvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.LifecycleType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TemporalityType;

public class KmehrMapper {

    public static CDLIFECYCLEvalues toLifeCycleValues(LifecycleType lifecycleType) {

        if (lifecycleType != null && lifecycleType.getCd() != null) {
            return lifecycleType.getCd().getValue();
        }
        return null;
    }

    public static CDTEMPORALITYvalues toTemporalityValues(TemporalityType temporalityType) {

        if (temporalityType != null && temporalityType.getCd() != null) {
            return temporalityType.getCd().getValue();
        }
        return null;
    }

}
