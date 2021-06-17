package org.imec.ivlab.datagenerator.uploader.model.instruction;

import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.datagenerator.uploader.model.action.Action;
import org.imec.ivlab.datagenerator.uploader.service.callback.Callback;

import java.io.File;
import java.util.List;

public interface Instruction<T extends Action> {

    T getAction();

    void setAction(T action);

    String getActorID();

    boolean isWriteAsIs();

    List<Callback> getCallbacks();

    Patient getPatient();

    TransactionType getTransactionType();

    File getFile();

}
