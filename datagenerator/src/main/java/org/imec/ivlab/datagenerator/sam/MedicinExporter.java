package org.imec.ivlab.datagenerator.sam;

import be.smals.sam.common.view.DeliveryEnvironmentType;
import be.smals.sam.common.view.DmppCodeTypeType;
import be.smals.sam.export.view.AmpDataType;
import be.smals.sam.export.view.AmpFullDataType;
import be.smals.sam.export.view.AmppComponentDataType;
import be.smals.sam.export.view.AmppComponentEquivalentDataType;
import be.smals.sam.export.view.AmppComponentEquivalentFullDataType;
import be.smals.sam.export.view.AmppComponentFullDataType;
import be.smals.sam.export.view.AmppFullDataType;
import be.smals.sam.export.view.DmppDataType;
import be.smals.sam.export.view.DmppFullDataType;
import be.smals.sam.export.view.ExportActualMedicinesType;
import be.smals.sam.ref.view.PackagingTypeType;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedicinExporter {

    private final static Logger LOG = LogManager.getLogger(MedicinExporter.class);

    SamProvider samProvider = new OfflineSamProvider();

    public static void main(String[] args) {

        // TODO: before running the main, uncomment following line in pom.xml of this project: <exclude>samexport${file.separator}**</exclude>
        // make sure to not commit the uncommented line since it will make the EVS install file grow by 12MB

        MedicinExporter medicinExporter = new MedicinExporter();
        List<Medication> medications = medicinExporter.getMedicationWithCNK();
        LOG.info("Got " + CollectionsUtil.size(medications) +  " medications");
        Collections.sort(medications, Collections.reverseOrder());
        for (Medication medication : medications) {
            String medicationString = StringEscapeUtils.escapeXml11(medication.getNotNullName());
            if (medication.getQuantity() != null) {
                medicationString = medicationString + " (" + medication.getQuantity().getValue().intValue() + " " + medication.getQuantity().getUnit() + ")";
            }
            LOG.info(medication.getCNK() + ";" + medicationString);
        }

    }



    public List<Medication> getMedicationWithCNK() {

        List<Medication> foundMedications = new ArrayList<>();

        ExportActualMedicinesType actualMedicines = samProvider.getActualMedicines();


        for (AmpFullDataType ampFullDataType : actualMedicines.getAmp()) {

            Medication medication = new Medication();
            List<AmpDataType> data = ampFullDataType.getData();
            for (AmpDataType ampDataType : data) {
                if (ampDataType.getTo() == null) {
                    if (ampDataType.getOfficialName() != null) {
                        medication.setOfficialName(ampDataType.getOfficialName());
                    }
                    if (ampDataType.getName() != null) {
                        medication.setName(ampDataType.getName().getNl());
                    }
                    if (ampDataType.getAbbreviatedName() != null) {
                        medication.setAbbreviatedName(ampDataType.getAbbreviatedName().getNl());
                    }
                    if (ampDataType.getPrescriptionName() != null) {
                        medication.setPrescriptionName(ampDataType.getPrescriptionName().getNl());
                    }
                }
            }

            Medication medicationClone = SerializationUtils.clone(medication);

            medicationClone = fillQuantity(ampFullDataType, medicationClone);
            if (medicationClone != null) {
                foundMedications.add(medicationClone);
            }

        }

        return foundMedications;
    }

    private Medication fillQuantity(AmpFullDataType ampFullDataType, Medication medicationClone) {
        for (AmppFullDataType amppFullDataType : ampFullDataType.getAmpp()) {
            for (DmppFullDataType dmppFullDataType : amppFullDataType.getDmpp()) {
                if (dmppFullDataType.getDeliveryEnvironment().equals(DeliveryEnvironmentType.P) && dmppFullDataType.getCodeType().equals(DmppCodeTypeType.CNK)) {

                    for (DmppDataType dmppDataType : dmppFullDataType.getData()) {
//                            LOG.info("Medication with CNK: " + dmppFullDataType.getCode() + " has from date: " + dmppDataType.getFrom() + " and to date: " + dmppDataType.getTo());
                        if (dmppDataType.getTo() == null) {
                            Quantity quantity = getQuantity(amppFullDataType);
                            if (quantity != null) {
                                String unit = StringUtils.replaceIgnoreCase(quantity.getUnit(), "{unit}", "stuks");
                                unit = RegExUtils.removeAll(unit, "[\\[\\]\\{\\}]");
                                quantity.setUnit(unit);
                                medicationClone.setQuantity(quantity);
                            }
                            medicationClone.setCNK(dmppFullDataType.getCode());
                            return medicationClone;
                        }
                    }

                }
            }
        }
        return null;
    }


    private Quantity getQuantity(AmppFullDataType amppFullDataType) {
        for (AmppComponentFullDataType ampComponentDataType : amppFullDataType.getAmppComponent()) {

            Quantity quantity = null;

            for (AmppComponentDataType amppComponentDataType : ampComponentDataType.getData()) {
                if (amppComponentDataType.getContentMultiplier() != null) {

                    PackagingTypeType packagingType = amppComponentDataType.getPackagingType();
                    if (packagingType != null && packagingType.getName() != null) {
                        quantity = new Quantity();
                        quantity.setValue(BigDecimal.valueOf(amppComponentDataType.getContentMultiplier()));
                        quantity.setUnit(packagingType.getName().getNl());
                        return quantity;
                    }

                }
            }


            for (AmppComponentEquivalentFullDataType amppComponentEquivalentFullDataType : ampComponentDataType.getAmppComponentEquivalent()) {
                for (AmppComponentEquivalentDataType amppComponentEquivalentDataType : amppComponentEquivalentFullDataType.getData()) {
                    if (amppComponentEquivalentDataType.getContent() != null) {
                        quantity = new Quantity();
                        if (quantity.getValue() == null) {
                            quantity.setValue(amppComponentEquivalentDataType.getContent().getValue());
                        }

                        quantity.setUnit(amppComponentEquivalentDataType.getContent().getUnit());
                        return quantity;
                    }
                }
            }
        }
        return null;
    }

}
