package org.imec.ivlab.core.kmehr.modifier.impl;

import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HeaderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.data.RIZIVData;
import org.imec.ivlab.core.kmehr.modifier.KmehrModification;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.patient.PatientMock;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.List;

public class DeanonymizationModifier implements KmehrModification {

    private Patient personPharmacist = PatientMock.getPatient();

    public DeanonymizationModifier() {

    }

    @Override
    public void modify(Kmehrmessage kmehrmessage) {

        modifyHCPartiesInHeader(kmehrmessage.getHeader());

        modifyHCPartiesInFolder(KmehrMessageUtil.getFolderType(kmehrmessage));

    }

    private void modifyHCPartiesInFolder(FolderType folderType) {

        if (folderType.getTransactions() == null) {
            return;
        }

        for (TransactionType transactionType : folderType.getTransactions()) {
            if (transactionType.getAuthor() == null || transactionType.getAuthor().getHcparties() == null) {
                continue;
            }

            modifyHCParties(transactionType.getAuthor().getHcparties());

        }


    }

    private void modifyHCPartiesInHeader(HeaderType header) {

        if (header == null) {
            return;
        }

        if (header.getSender() == null || header.getSender().getHcparties() == null) {
            return;
        }

        modifyHCParties(header.getSender().getHcparties());
    }

    private void modifyHCParties(List<HcpartyType> hcparties) {

        for (HcpartyType hcparty : hcparties) {

            List<IDHCPARTY> ids = hcparty.getIds();
            if (CollectionsUtil.emptyOrNull(ids)) {
                continue;
            }

            for (IDHCPARTY id : ids) {

                if (id.getS() != null && id.getS().equals(IDHCPARTYschemes.ID_HCPARTY) && isAnonymized(id.getValue())) {
                    id.setValue(RIZIVData.DR_VEEERLE_MOERMANS.getValue());
                    continue;
                }

                if (id.getS() != null && id.getS().equals(IDHCPARTYschemes.INSS) && isAnonymized(id.getValue())) {
                    id.setValue(personPharmacist.getId());
                    continue;
                }

            }

            if (isAnonymized(hcparty.getFirstname())) {
                hcparty.setFirstname(personPharmacist.getFirstName());
            }

            if (isAnonymized(hcparty.getFamilyname())) {
                hcparty.setFamilyname(personPharmacist.getLastName());
            }

            if (isAnonymized(hcparty.getName())) {
                hcparty.setName("");
            }

        }

    }

    private boolean isAnonymized(String value) {

        if (StringUtils.startsWith(value, "__") && StringUtils.endsWith(value, "__")) {
            return true;
        }

        return false;

    }
}
