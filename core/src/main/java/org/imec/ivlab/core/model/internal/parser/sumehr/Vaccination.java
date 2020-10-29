package org.imec.ivlab.core.model.internal.parser.sumehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.imec.ivlab.core.model.internal.mapper.medication.Identifier;
import org.imec.ivlab.core.model.internal.parser.AbstractParsedItem;

public class Vaccination extends AbstractParsedItem<ItemType> {

    private String vaccinatedAgainst;
    private Identifier identifier;
    private LocalDate applicationDate;
    private List<TextType> textTypes;
    private List<CDCONTENT> cdcontents = new ArrayList<>();
    private CDLIFECYCLEvalues lifecycle;
    private LocalDateTime recordDateTime;

    public Vaccination() {
        super("item");
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public List<TextType> getTextTypes() {
        return textTypes;
    }

    public void setTextTypes(List<TextType> textTypes) {
        this.textTypes = textTypes;
    }

    public List<CDCONTENT> getCdcontents() {
        return cdcontents;
    }

    public void setCdcontents(List<CDCONTENT> cdcontents) {
        this.cdcontents = cdcontents;
    }

    public String getVaccinatedAgainst() {
        return vaccinatedAgainst;
    }

    public void setVaccinatedAgainst(String vaccinatedAgainst) {
        this.vaccinatedAgainst = vaccinatedAgainst;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public CDLIFECYCLEvalues getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(CDLIFECYCLEvalues lifecycle) {
        this.lifecycle = lifecycle;
    }

    public LocalDateTime getRecordDateTime() {
        return recordDateTime;
    }

    public void setRecordDateTime(LocalDateTime recordDateTime) {
        this.recordDateTime = recordDateTime;
    }

}
