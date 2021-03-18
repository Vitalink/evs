package org.imec.ivlab.core.model.patient;

import org.imec.ivlab.core.model.patient.model.Gender;
import org.imec.ivlab.core.model.patient.model.Patient;

import java.time.LocalDate;

public class PatientMock {

    public static Patient getPatient() {

        Patient patient = new Patient("90010100101", "John", "Doe", Gender.MALE, LocalDate.of(1990, 1, 1));
        return patient;

    }

}
