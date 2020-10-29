package org.imec.ivlab.core.model.patient.model;

import org.imec.ivlab.core.model.patient.adapter.GenderAdapter;
import org.imec.ivlab.core.xml.LocalDateAdapter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@XmlRootElement(name="patient")
public class Patient {

    private String key;
    private String id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthDate;
    private String usualLanguage;

    public Patient() {
    }

    public Patient(String id, String firstName, String lastName, Gender gender, LocalDate birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @XmlJavaTypeAdapter(value = GenderAdapter.class)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUsualLanguage() {
        return usualLanguage;
    }

    public void setUsualLanguage(String usualLanguage) {
        this.usualLanguage = usualLanguage;
    }

    public String inReadableFormat() {
        StringBuffer stringBuffer = new StringBuffer(getKey());
        stringBuffer.append(" (");
        stringBuffer.append(getId());
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    @Override
    public String toString() {
        return this.inReadableFormat();
    }
}
