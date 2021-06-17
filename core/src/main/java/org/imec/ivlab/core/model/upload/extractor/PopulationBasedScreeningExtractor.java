package org.imec.ivlab.core.model.upload.extractor;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;

public class PopulationBasedScreeningExtractor extends KmehrWithReferenceExtractor {

    @Override
    protected CDTRANSACTIONvalues getCdTransactionValues() {
        return CDTRANSACTIONvalues.POPULATION_BASED_SCREENING;
    }

}
