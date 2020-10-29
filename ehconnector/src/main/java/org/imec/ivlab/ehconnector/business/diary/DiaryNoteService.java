package org.imec.ivlab.ehconnector.business.diary;

import org.imec.ivlab.core.authentication.model.AuthenticationConfig;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.diarylist.DiaryNoteList;
import org.imec.ivlab.core.model.patient.model.Patient;

public interface DiaryNoteService {

    DiaryNoteList getDiaryNoteList(Patient patient) throws VitalinkException;

    void putTransactions(Patient patient, DiaryNoteList diaryNoteList) throws VitalinkException;

    void revokeTransactions(Patient patient, DiaryNoteList diaryNoteList) throws VitalinkException;

    void authenticate(AuthenticationConfig authenticationConfig) throws VitalinkException;

}
