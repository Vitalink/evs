package org.imec.ivlab.datagenerator.exporter.monitor;


import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.patient.model.Patient;

public class MonitorInstruction {

    private Patient patient;
    private TransactionType transactionType;
    private String actorID;
    private boolean breakTheGlassIfTRMissing;

    public MonitorInstruction(TransactionType transactionType, Patient patient, String actorID) {
        this.transactionType = transactionType;
        this.patient = patient;
        this.actorID = actorID;
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

    public boolean isBreakTheGlassIfTRMissing() {
        return breakTheGlassIfTRMissing;
    }

    public void setBreakTheGlassIfTRMissing(boolean breakTheGlassIfTRMissing) {
        this.breakTheGlassIfTRMissing = breakTheGlassIfTRMissing;
    }

    public String getActorID() {
        return actorID;
    }

    public void setActorID(String actorID) {
        this.actorID = actorID;
    }
}
