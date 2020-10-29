package org.imec.ivlab.core.kmehr.modifier.impl;


import be.fgov.ehealth.standards.kmehr.cd.v1.CDSTANDARD;
import be.fgov.ehealth.standards.kmehr.schema.v1.HeaderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.StandardType;
import org.imec.ivlab.core.kmehr.modifier.KmehrModification;

public class KmehrStandardModifier implements KmehrModification {

    private String kmehrStandard;

    public KmehrStandardModifier(String kmehrStandard) {
        this.kmehrStandard = kmehrStandard;
    }

    @Override
    public void modify(Kmehrmessage kmehrmessage) {

        if (kmehrmessage == null) {
            return;
        }

        HeaderType header;
        if (kmehrmessage.getHeader() != null) {
            header = kmehrmessage.getHeader();
        } else {
            header = new HeaderType();
        }

        StandardType standard;
        if (header.getStandard() != null) {
             standard = kmehrmessage.getHeader().getStandard();
        } else {
            standard = new StandardType();
        }

        CDSTANDARD cd;
        if (standard.getCd() == null) {
            cd = standard.getCd();
            cd.setValue(kmehrStandard);
        } else {
            cd = new CDSTANDARD();
            cd.setSV("1.4");
            cd.setS("CD-STANDARD");
            cd.setValue(kmehrStandard);
            standard.setCd(cd);
        }


    }

}
