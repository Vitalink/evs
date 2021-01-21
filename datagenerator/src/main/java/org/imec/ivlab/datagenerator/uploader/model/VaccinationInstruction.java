package org.imec.ivlab.datagenerator.uploader.model;

import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.upload.vaccinationentry.VaccinationList;

public class VaccinationInstruction extends AbstractInstruction<VaccinationAction> {

    private VaccinationList vaccinationList;
    private VaccinationAction action;

    public VaccinationInstruction() {
        super(TransactionType.VACCINATION);
    }

    public VaccinationList getVaccinationList() {
        return vaccinationList;
    }

    public void setVaccinationList(VaccinationList vaccinationList) {
        this.vaccinationList = vaccinationList;
    }

    @Override
    public VaccinationAction getAction() {
        return action;
    }

    public void setAction(VaccinationAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "VaccinationInstruction{" +
            "vaccinationList=" + vaccinationList +
            ", action=" + action +
            '}';
    }
}
