package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IDKmehrUtil {

    /**
     * Get a list of id nodes where the S attribute equals the {@code idkmehRschemesFilter}
     * @param idkmehrsIn
     * @param idkmehRschemesFilter
     * @return
     */
    public static List<IDKMEHR> getIDKmehrs(List<IDKMEHR> idkmehrsIn, IDKMEHRschemes idkmehRschemesFilter) {

        List<IDKMEHR> idkmehrsOut = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(idkmehrsIn)) {
            return idkmehrsOut;
        }

        for (IDKMEHR idkmehr : idkmehrsIn) {

            if (StringUtils.equalsIgnoreCase(idkmehr.getS().value(), idkmehRschemesFilter.value())) {
                idkmehrsOut.add(idkmehr);
            }

        }

        return idkmehrsOut;

    }

    public static void removeIDKmehrs(List<IDKMEHR> idkmehrsIn, IDKMEHRschemes idkmehRschemesFilter) {

        if (CollectionsUtil.emptyOrNull(idkmehrsIn)) {
            return;
        }

        Iterator<IDKMEHR> iterator = idkmehrsIn.iterator();

        while (iterator.hasNext()) {
            IDKMEHR idkmehr = iterator.next();
            if (idkmehRschemesFilter.equals(idkmehr.getS())) {
                iterator.remove();
            }
        }

    }

    public static void addIDKmehrs(List<IDKMEHR> idkmehrsIn, List<IDKMEHR> idkmehrsToAdd) {

        if (CollectionsUtil.emptyOrNull(idkmehrsIn)) {
            idkmehrsIn = new ArrayList<>();
        }

        if (idkmehrsToAdd == null) {
            return;
        }

        idkmehrsIn.addAll(idkmehrsToAdd);

    }

}
