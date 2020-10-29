package org.imec.ivlab.datagenerator.uploader.service;

import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.diarylist.DiaryNoteList;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;

public interface DiaryNoteUploader {

    void add(Patient patient, DiaryNoteList diaryNoteList, String actorId) throws UploaderException, VitalinkException;

    void generateREF(Patient patient, String actorId) throws VitalinkException, UploaderException;

    void updateREF(Patient patient, DiaryNoteList diaryNoteList, String actorId) throws VitalinkException, UploaderException;

}
