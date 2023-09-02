package org.imec.ivlab.core.kmehr.modifier.impl;


import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.kmehr.modifier.impl.util.HeaderUtil;
import org.imec.ivlab.core.kmehr.modifier.KmehrModification;

import org.joda.time.LocalDateTime;

public class KmehrHeaderModifier implements KmehrModification {

    private LocalDateTime localDateTime;

    public KmehrHeaderModifier(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public void modify(Kmehrmessage kmehrmessage) {

        try {
            kmehrmessage.setHeader(HeaderUtil.createHeader(localDateTime));
        } catch (TransformationException e) {
            throw new RuntimeException(e);
        }

    }

}
