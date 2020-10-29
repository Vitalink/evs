package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import org.apache.commons.collections.CollectionUtils;
import org.imec.ivlab.core.exceptions.DataNotFoundException;
import org.imec.ivlab.core.exceptions.MultipleEntitiesFoundException;

import java.util.ArrayList;
import java.util.List;

public class ContentUtil {

    /**
     * Get a list of cd nodes where the S attribute equals the {@code cdContentSchemeFilter}
     * @param content
     * @param cdContentSchemeFilter
     * @return
     */
    public static List<CDCONTENT> getCDContents(ContentType content, CDCONTENTschemes cdContentSchemeFilter) {

        List<CDCONTENT> cdcontents = new ArrayList<>();

        if (content == null) {
            return cdcontents;
        }

        return CDContentUtil.getCDContents(content.getCds(), cdContentSchemeFilter);

    }

    public static CDCONTENT getCDContent(ContentType content, CDCONTENTschemes cdContentSchemeFilter) {

        List<CDCONTENT> cdcontents = getCDContents(content, cdContentSchemeFilter);

        if (CollectionUtils.isEmpty(cdcontents)) {
            throw new DataNotFoundException("No content found with S: " + cdContentSchemeFilter);
        }

        if (CollectionUtils.size(cdcontents) > 1) {
            throw new MultipleEntitiesFoundException("Multiple contents found with S-: " + cdContentSchemeFilter);
        }

        return cdcontents.get(0);

    }


}
