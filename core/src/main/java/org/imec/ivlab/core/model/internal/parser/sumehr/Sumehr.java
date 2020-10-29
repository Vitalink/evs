package org.imec.ivlab.core.model.internal.parser.sumehr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.imec.ivlab.core.model.internal.parser.ParsedKmehrItem;

public class Sumehr extends ParsedKmehrItem {

    private List<PatientWill> patientWills = new ArrayList<>();
    private List<HcParty> gmdManagers = new ArrayList<>();
    private List<HcParty> contactHCParties = new ArrayList<>();
    private List<ContactPerson> contactPersons = new ArrayList<>();
    private List<MedicationEntrySumehr> medicationEntries = new ArrayList<>();
    private List<HealthCareElement> healthCareElements = new ArrayList<>();
    private List<Vaccination> vaccinations = new ArrayList<>();
    private List<Risk> socialRisks = new ArrayList<>();
    private List<Risk> risks = new ArrayList<>();
    private List<Risk> allergies = new ArrayList<>();
    private List<Risk> adrs = new ArrayList<>();

    @Override
    public Set<String> ignoredNodeNames() {
        HashSet ignoredNames = new HashSet();
        ignoredNames.add("iscomplete");
        ignoredNames.add("isvalidated");
        ignoredNames.add("text");
        return ignoredNames;
    }

    public List<PatientWill> getPatientWills() {
        return patientWills;
    }

    public void setPatientWills(List<PatientWill> patientWills) {
        this.patientWills = patientWills;
    }

    public List<HcParty> getGmdManagers() {
        return gmdManagers;
    }

    public void setGmdManagers(List<HcParty> gmdManagers) {
        this.gmdManagers = gmdManagers;
    }

    public List<HcParty> getContactHCParties() {
        return contactHCParties;
    }

    public void setContactHCParties(List<HcParty> contactHCParties) {
        this.contactHCParties = contactHCParties;
    }

    public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public List<MedicationEntrySumehr> getMedicationEntries() {
        return medicationEntries;
    }

    public void setMedicationEntries(List<MedicationEntrySumehr> medicationEntries) {
        this.medicationEntries = medicationEntries;
    }

    public List<HealthCareElement> getHealthCareElements() {
        return healthCareElements;
    }

    public void setHealthCareElements(List<HealthCareElement> healthCareElements) {
        this.healthCareElements = healthCareElements;
    }

    public List<Vaccination> getVaccinations() {
        return vaccinations;
    }

    public void setVaccinations(List<Vaccination> vaccinations) {
        this.vaccinations = vaccinations;
    }

    public List<Risk> getSocialRisks() {
        return socialRisks;
    }

    public void setSocialRisks(List<Risk> socialRisks) {
        this.socialRisks = socialRisks;
    }

    public List<Risk> getRisks() {
        return risks;
    }

    public void setRisks(List<Risk> risks) {
        this.risks = risks;
    }

    public List<Risk> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<Risk> allergies) {
        this.allergies = allergies;
    }

    public List<Risk> getAdrs() {
        return adrs;
    }

    public void setAdrs(List<Risk> adrs) {
        this.adrs = adrs;
    }
}
