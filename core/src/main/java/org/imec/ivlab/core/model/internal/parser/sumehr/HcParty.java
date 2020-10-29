package org.imec.ivlab.core.model.internal.parser.sumehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.schema.v1.AddressType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TelecomType;
import org.imec.ivlab.core.model.internal.parser.AbstractParsedItem;

import java.util.List;

public class HcParty extends AbstractParsedItem<HcpartyType> {

    public HcParty() {
        super("hcparty");
    }

    private List<IDHCPARTY> ids;
    private List<CDHCPARTY> cds;
    private String firstname;
    private String familyname;
    private String name;
    private List<AddressType> addresses;
    private List<TelecomType> telecoms;

    public List<IDHCPARTY> getIds() {
        return ids;
    }

    public void setIds(List<IDHCPARTY> ids) {
        this.ids = ids;
    }

    public List<CDHCPARTY> getCds() {
        return cds;
    }

    public void setCds(List<CDHCPARTY> cds) {
        this.cds = cds;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFamilyname() {
        return familyname;
    }

    public void setFamilyname(String familyname) {
        this.familyname = familyname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AddressType> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressType> addresses) {
        this.addresses = addresses;
    }

    public List<TelecomType> getTelecoms() {
        return telecoms;
    }

    public void setTelecoms(List<TelecomType> telecoms) {
        this.telecoms = telecoms;
    }
}
