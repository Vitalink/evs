package org.imec.ivlab.datagenerator.uploader.service;

import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.model.upload.sumehrlist.SumehrList;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;

public interface SumehrUploader {

    void add(Patient patient, SumehrList sumehrList, String actorId) throws UploaderException, VitalinkException;

    void empty(Patient patient, String actorId) throws VitalinkException, UploaderException;

    void replace(Patient patient, SumehrList sumehrList, String actorId) throws VitalinkException, UploaderException;

    void generateREF(Patient patient, String actorId) throws VitalinkException, UploaderException;

    void removeREF(Patient patient, SumehrList sumehrList, String actorId) throws VitalinkException, UploaderException;

    void updateREF(Patient patient, SumehrList sumehrList, String actorId) throws VitalinkException, UploaderException;

}
