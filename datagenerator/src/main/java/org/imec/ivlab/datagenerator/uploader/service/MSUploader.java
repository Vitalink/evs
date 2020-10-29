package org.imec.ivlab.datagenerator.uploader.service;

import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.datagenerator.uploader.dateshift.ShiftAction;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;

public interface MSUploader {

    void add(Patient patient, MSEntryList msEntryList, String actorId) throws UploaderException, VitalinkException;

    void replace(Patient patient, MSEntryList msEntryList, String actorId) throws VitalinkException, UploaderException;

    void generateREF(Patient patient, MSEntryList msEntryList, String actorId) throws VitalinkException, UploaderException;

    void removeREF(Patient patient, MSEntryList msEntryList, String actorId) throws VitalinkException, UploaderException;

    void updateschemeREF(Patient patient, MSEntryList msEntryList, String actorId) throws VitalinkException, UploaderException;

    void setStartTransactionId(String startTransactionId);

    void setShiftAction(ShiftAction shiftAction);

}
