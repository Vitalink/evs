package org.imec.ivlab.core.model.internal.mapper.medication;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTEMPORALITYvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import org.imec.ivlab.core.kmehr.model.FrequencyCode;
import org.imec.ivlab.core.model.internal.parser.AbstractParsedItem;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class MedicationEntryBasic extends AbstractParsedItem<ItemType> implements Serializable {

    private Identifier identifier;
    private Route route;
    private String instructionForPatient;
    private String instructionForOverdosing;
    private FrequencyCode frequencyCode;
    private String compoundPrescription;

    private List<IndividualDayperiod> dayperiods;
    private LocalDate beginDate;
    private LocalDate endDate;

    private Duration duration;

    private CDTEMPORALITYvalues temporality;

    public MedicationEntryBasic() {
        super("item");
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getInstructionForPatient() {
        return instructionForPatient;
    }

    public void setInstructionForPatient(String instructionForPatient) {
        this.instructionForPatient = instructionForPatient;
    }

    public FrequencyCode getFrequencyCode() {
        return frequencyCode;
    }

    public void setFrequencyCode(FrequencyCode frequencyCode) {
        this.frequencyCode = frequencyCode;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public CDTEMPORALITYvalues getTemporality() {
        return temporality;
    }

    public void setTemporality(CDTEMPORALITYvalues temporality) {
        this.temporality = temporality;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getCompoundPrescription() {
        return compoundPrescription;
    }

    public void setCompoundPrescription(String compoundPrescription) {
        this.compoundPrescription = compoundPrescription;
    }

    public String getInstructionForOverdosing() {
        return instructionForOverdosing;
    }

    public void setInstructionForOverdosing(String instructionForOverdosing) {
        this.instructionForOverdosing = instructionForOverdosing;
    }

    public List<IndividualDayperiod> getDayperiods() {
        return dayperiods;
    }

    public void setDayperiods(List<IndividualDayperiod> dayperiods) {
        this.dayperiods = dayperiods;
    }
}
