package org.imec.ivlab.core.kmehr.modifier;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;

public interface KmehrModification {

    void modify(Kmehrmessage kmehrmessage);

}
