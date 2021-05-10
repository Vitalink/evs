package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.List;

public class IDPatientUtil {

    /**
     * Get a list of id nodes where the S attribute equals the {@code idpatienTschemesFilter}
     * @param idpatientsIn
     * @param idpatienTschemesFilter
     * @return
     */
    public static List<IDPATIENT> getIDPatients(List<IDPATIENT> idpatientsIn, IDPATIENTschemes idpatienTschemesFilter) {

        List<IDPATIENT> idpatientsOut = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(idpatientsIn)) {
            return idpatientsOut;
        }

        for (IDPATIENT idpatient : idpatientsIn) {

            if (StringUtils.equalsIgnoreCase(idpatient.getS().value(), idpatienTschemesFilter.value())) {
                idpatientsOut.add(idpatient);
            }

        }

        return idpatientsOut;

    }

}
