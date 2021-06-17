package org.imec.ivlab.core.model.upload.extractor;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;

public class ChildPreventionExtractor extends KmehrWithReferenceExtractor {

    @Override
    protected CDTRANSACTIONvalues getCdTransactionValues() {
        return CDTRANSACTIONvalues.CHILD_PREVENTION;
    }

}
