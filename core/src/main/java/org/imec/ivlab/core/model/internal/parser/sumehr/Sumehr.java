package org.imec.ivlab.core.model.internal.parser.sumehr;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.model.internal.parser.ParsedKmehrItem;

@Getter
@Setter
public class Sumehr extends ParsedKmehrItem {

    private List<PatientWill> patientWills = new ArrayList<>();
    private List<HcParty> gmdManagers = new ArrayList<>();
    private List<HcParty> contactHCParties = new ArrayList<>();
    private List<ContactPerson> contactPersons = new ArrayList<>();
    private List<MedicationEntrySumehr> medicationEntries = new ArrayList<>();
    private List<HealthCareElement> healthCareElements = new ArrayList<>();
    private List<Treatment> treatments = new ArrayList<>();
    private List<Problem> problems = new ArrayList<>();
    private List<Vaccination> vaccinations = new ArrayList<>();
    private List<Risk> socialRisks = new ArrayList<>();
    private List<Risk> risks = new ArrayList<>();
    private List<Risk> allergies = new ArrayList<>();
    private List<Risk> adrs = new ArrayList<>();
    private List<TextType> textTypes = new ArrayList<>();
    private String evsRef;

    @Override
    public Set<String> ignoredNodeNames() {
        HashSet ignoredNames = new HashSet();
        ignoredNames.add("iscomplete");
        ignoredNames.add("isvalidated");
        ignoredNames.add("text");
        return ignoredNames;
    }

}
