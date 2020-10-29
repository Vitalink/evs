package org.imec.ivlab.datagenerator.sam;

import be.smals.sam.export.view.ExportActualMedicinesType;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.core.util.JAXBUtils;

public class OfflineSamProvider implements SamProvider {

    @Override
    public ExportActualMedicinesType getActualMedicines() {
        String resourceAsString = IOUtils.getResourceAsString("/sam/AMP-1530615628300.xml");
        try {
            return JAXBUtils.unmarshal(ExportActualMedicinesType.class, resourceAsString);
        } catch (TransformationException e) {
            throw new RuntimeException(e);
        }
    }

}
