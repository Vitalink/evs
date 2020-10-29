package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEM;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.List;

public class CDItemUtil {

    /**
     * Get a list of cd nodes where the S attribute equals the {@code cditeMschemesFilter}
     * @param cditemsIn
     * @param cditeMschemesFilter
     * @return
     */
    public static List<CDITEM> getCDItems(List<CDITEM> cditemsIn, CDITEMschemes cditeMschemesFilter) {

        List<CDITEM> cditemsOut = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(cditemsIn)) {
            return cditemsOut;
        }

        for (CDITEM cditem : cditemsIn) {

            if (StringUtils.equalsIgnoreCase(cditem.getS().value(), cditeMschemesFilter.value())) {
                cditemsOut.add(cditem);
            }

        }

        return cditemsOut;

    }

}
