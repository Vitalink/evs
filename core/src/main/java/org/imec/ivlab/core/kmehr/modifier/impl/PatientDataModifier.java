package org.imec.ivlab.core.kmehr.modifier.impl;


import be.fgov.ehealth.standards.kmehr.cd.v1.CDSEX;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.DateType;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.PersonType;
import be.fgov.ehealth.standards.kmehr.schema.v1.SexType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.kmehr.modifier.KmehrModification;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.kmehr.mapper.ToKmehrMapper;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.util.DateUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

public class PatientDataModifier implements KmehrModification {

    private final static Logger log = LogManager.getLogger(PatientDataModifier.class);

    private Patient patient;

    public PatientDataModifier(Patient patient) {
        this.patient = patient;
    }

    @Override
    public void modify(Kmehrmessage kmehrmessage) {

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);

        PersonType person = folderType.getPatient();

        if (patient == null) {
            log.debug("No patient data specified.");
            return;
        }

        if (person == null) {
            person = new PersonType();
        }

        if (person.getBirthdate() != null) {
            DateType birtyDayDateType = new DateType();
            try {
                XMLGregorianCalendar xmlGregorianCalendar = DateUtils.toXmlGregorianCalendar(patient.getBirthDate());
                xmlGregorianCalendar.setTimezone(0);
                birtyDayDateType.setDate(xmlGregorianCalendar);
                person.setBirthdate(birtyDayDateType);
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException(e);
            }

        }

        SexType sexType = new SexType();
        CDSEX cdSex = new CDSEX();
        cdSex.setValue(ToKmehrMapper.genderToCDSEXValues(patient.getGender()));
        cdSex.setSV("1.0");
        cdSex.setS("CD-SEX");
        sexType.setCd(cdSex);
        person.setSex(sexType);

        person.setFamilyname(patient.getLastName());

        person.getFirstnames().clear();
        person.getFirstnames().add(patient.getFirstName());

        person.getIds().clear();
        IDPATIENT IDPatient = new IDPATIENT();
        IDPatient.setValue(patient.getId());
        IDPatient.setS(IDPATIENTschemes.ID_PATIENT);
        IDPatient.setSV("1.0");
        person.getIds().add(IDPatient);

        person.setUsuallanguage(patient.getUsualLanguage());

    }

}
