package org.imec.ivlab.core.model.patient;

import org.imec.ivlab.core.model.patient.model.Gender;
import org.imec.ivlab.core.model.patient.model.Patient;

import java.time.LocalDate;

public class PatientMock {

    public static Patient getPatient() {

        Patient patient = new Patient("72071135503", "Jeroen", "Brackez", Gender.MALE, LocalDate.of(1972, 07, 11));
        return patient;

    }

}
