package org.imec.ivlab.core.kmehr.model.localid;

import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.localid.util.URIBuilder;
import org.imec.ivlab.core.util.CollectionsUtil;

public class LocalIdParser {

    public static boolean isVitalinkLocalId(IDKMEHR idKmehr) {
        return StringUtils.equalsIgnoreCase(idKmehr.getSL(), "vitalinkuri");
    }

    public static List<LocalId> parseLocalIds(List<IDKMEHR> idKmehrs) {

        if (CollectionsUtil.emptyOrNull(idKmehrs)) {
            return null;
        }

        List<LocalId> localIds = new ArrayList<>();

        for (IDKMEHR idKmehr : idKmehrs) {
            localIds.add(idkmehrToLocalId(idKmehr));
        }

        return localIds;

    }

    private static LocalId idkmehrToLocalId(IDKMEHR idKmehr) {
        if (isVitalinkLocalId(idKmehr)) {
            URI uri = URIBuilder.fromString(idKmehr.getValue());
            return uri;
        } else {
            return new GenericLocalId(idKmehr.getValue());
        }

    }

}
