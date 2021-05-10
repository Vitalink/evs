package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.apache.commons.collections.CollectionUtils;
import org.imec.ivlab.core.exceptions.DataNotFoundException;

public class KmehrMessageUtil {

    public static FolderType getFolderType(Kmehrmessage kmehrmessage) {

        if (kmehrmessage == null) {
            return null;
        }

        if (CollectionUtils.isEmpty(kmehrmessage.getFolders())) {
            throw new DataNotFoundException("No folders found in kmehr");
        }

        return kmehrmessage.getFolders().get(0);

    }

}
