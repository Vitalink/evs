package org.imec.ivlab.ehconnector.business.diary;

import org.imec.ivlab.core.authentication.model.AuthenticationConfig;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;

public interface DiaryNoteService {

    KmehrWithReferenceList getKmehrWithReferenceList(Patient patient) throws VitalinkException;

    void putTransactions(Patient patient, KmehrWithReferenceList diaryNoteList) throws VitalinkException;

    void revokeTransactions(Patient patient, KmehrWithReferenceList diaryNoteList) throws VitalinkException;

    void authenticate(AuthenticationConfig authenticationConfig) throws VitalinkException;

}
