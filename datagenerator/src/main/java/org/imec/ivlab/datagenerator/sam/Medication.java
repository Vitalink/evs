package org.imec.ivlab.datagenerator.sam;

import java.io.Serializable;

public class Medication implements Serializable, Comparable<Medication> {
        private String name;
        private String officialName;
        private String abbreviatedName;
        private String prescriptionName;
        private String CNK;
        private Quantity quantity;

        public String getNotNullName() {
            if (name != null) {
                return name;
            }
            if (abbreviatedName != null) {
                return abbreviatedName;
            }
            if (officialName != null) {
                return officialName;
            }
            if (prescriptionName != null) {
                return prescriptionName;
            }
            return null;
        }

    public Quantity getQuantity() {
        return quantity;
    }

    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    @Override
        public int compareTo(Medication anotherMedication) {
            return anotherMedication.getName().compareTo(this.name);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName;
        }

        public String getAbbreviatedName() {
            return abbreviatedName;
        }

        public void setAbbreviatedName(String abbreviatedName) {
            this.abbreviatedName = abbreviatedName;
        }

        public String getPrescriptionName() {
            return prescriptionName;
        }

        public void setPrescriptionName(String prescriptionName) {
            this.prescriptionName = prescriptionName;
        }

        public String getCNK() {
            return CNK;
        }

        public void setCNK(String CNK) {
            this.CNK = CNK;
        }
    }