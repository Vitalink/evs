package org.imec.ivlab.ehconnector.business.medicationscheme;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.authentication.model.AuthenticationConfig;
import org.imec.ivlab.core.model.patient.model.Patient;

public interface MSService {

    MSEntryList getMedicationScheme(Patient patient) throws VitalinkException;

    void putMedicationScheme(Patient patient, Kmehrmessage kmehrmessageTemplate, MSEntryList msEntryList) throws VitalinkException;

    void authenticate(AuthenticationConfig authenticationConfig) throws VitalinkException;

}
