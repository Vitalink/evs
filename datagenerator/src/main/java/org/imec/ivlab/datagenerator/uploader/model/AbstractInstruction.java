package org.imec.ivlab.datagenerator.uploader.model;

import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.datagenerator.uploader.service.callback.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInstruction<T extends Action> implements Instruction<T> {

    private Patient patient;
    private String actorID;
    private boolean writeAsIs;
    private List<Callback> callbacks = new ArrayList<>();
    private TransactionType transactionType;
    private File file;

    public AbstractInstruction(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getActorID() {
        return actorID;
    }

    public void setActorID(String actorID) {
        this.actorID = actorID;
    }

    public boolean isWriteAsIs() {
        return writeAsIs;
    }

    public void setWriteAsIs(boolean writeAsIs) {
        this.writeAsIs = writeAsIs;
    }

    public List<Callback> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(List<Callback> callbacks) {
        this.callbacks = callbacks;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                ", patient=" + patient +
                ", actorID='" + actorID + '\'' +
                ", writeAsIs=" + writeAsIs +
                ", callbacks=" + callbacks +
                ", transactionType=" + transactionType +
                ", file=" + file +
                '}';
    }
}
