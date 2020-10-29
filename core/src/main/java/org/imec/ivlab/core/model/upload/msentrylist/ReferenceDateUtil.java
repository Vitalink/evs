package org.imec.ivlab.core.model.upload.msentrylist;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.PersonType;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.util.DateUtils;

import java.time.LocalDate;

public class ReferenceDateUtil {

    public static LocalDate getReferenceDate(Kmehrmessage kmehrmessage) {

        PersonType patient = KmehrMessageUtil.getFolderType(kmehrmessage).getPatient();

        if (patient == null || patient.getRecorddatetime() == null) {
            return null;
        }

        return DateUtils.toLocalDateTime(patient.getRecorddatetime()).toLocalDate();

    }

    public static void setReferenceDate(Kmehrmessage kmehrmessage, LocalDate referenceDate) {

        PersonType patient = KmehrMessageUtil.getFolderType(kmehrmessage).getPatient();

        if (patient == null) {
            patient = new PersonType();
        }

        patient.setRecorddatetime(DateUtils.toCalendar(referenceDate));

    }

}
