package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.List;

public class CDContentUtil {

    /**
     * Get a list of cd nodes where the S attribute equals the {@code cdcontentSchemesFilter}
     * @param cdContentsIn
     * @param cdcontentSchemesFilter
     * @return
     */
    public static List<CDCONTENT> getCDContents(List<CDCONTENT> cdContentsIn, CDCONTENTschemes cdcontentSchemesFilter) {

        List<CDCONTENT> cdContentsOut = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(cdContentsIn)) {
            return cdContentsOut;
        }

        for (CDCONTENT cdcontent : cdContentsIn) {

            if (StringUtils.equalsIgnoreCase(cdcontent.getS().value(), cdcontentSchemesFilter.value())) {
                cdContentsOut.add(cdcontent);
            }

        }

        return cdContentsOut;

    }

    public static List<String> toStrings(List<CDCONTENT>  cdcontents) {

        if (cdcontents == null) {
            return null;
        }

        List<String> strings = new ArrayList<>();

        for (CDCONTENT cdcontent : cdcontents) {
            if (cdcontent != null) {
                strings.add(cdcontent.getValue());
            }
        }

        return strings;

    }

}
