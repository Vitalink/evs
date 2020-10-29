package org.imec.ivlab.core.model.internal.parser.sumehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDSEXvalues;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;
import be.fgov.ehealth.standards.kmehr.schema.v1.AddressType;
import be.fgov.ehealth.standards.kmehr.schema.v1.PersonType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TelecomType;
import org.imec.ivlab.core.model.internal.parser.AbstractParsedItem;

import java.time.LocalDate;
import java.util.List;

public abstract class AbstractPerson extends AbstractParsedItem<PersonType> {

    private List<IDPATIENT> ids;
    private LocalDate birthdate;
    private LocalDate deathdate;
    private String familyname;
    private List<String> firstnames;
    private String usuallanguage;
    private List<TelecomType> telecoms;
    private List<AddressType> addresses;
    private CDSEXvalues sex;

    public AbstractPerson(String rootElementName) {
        super(rootElementName);
    }

    public List<IDPATIENT> getIds() {
        return ids;
    }

    public void setIds(List<IDPATIENT> ids) {
        this.ids = ids;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public LocalDate getDeathdate() {
        return deathdate;
    }

    public void setDeathdate(LocalDate deathdate) {
        this.deathdate = deathdate;
    }

    public String getFamilyname() {
        return familyname;
    }

    public void setFamilyname(String familyname) {
        this.familyname = familyname;
    }

    public List<String> getFirstnames() {
        return firstnames;
    }

    public void setFirstnames(List<String> firstnames) {
        this.firstnames = firstnames;
    }

    public String getUsuallanguage() {
        return usuallanguage;
    }

    public void setUsuallanguage(String usuallanguage) {
        this.usuallanguage = usuallanguage;
    }

    public List<TelecomType> getTelecoms() {
        return telecoms;
    }

    public void setTelecoms(List<TelecomType> telecoms) {
        this.telecoms = telecoms;
    }

    public List<AddressType> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressType> addresses) {
        this.addresses = addresses;
    }

    public CDSEXvalues getSex() {
        return sex;
    }

    public void setSex(CDSEXvalues sex) {
        this.sex = sex;
    }
}
