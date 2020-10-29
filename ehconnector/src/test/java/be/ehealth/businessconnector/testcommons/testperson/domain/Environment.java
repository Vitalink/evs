/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.testperson.domain;


/**
 * Information of a person for an environment.
 * 
 * @author EHP
 */
public class Environment {

    private String p12Password;

    private String p12Location;

    private String environmentName;

    private Person person;

    /**
     * @param person
     */
    public Environment(Person person) {
        super();
        this.person = person;
    }


    /**
     * @return the p12Password
     */
    public String getP12Password() {
        return p12Password;
    }


    /**
     * @param p12Password the p12Password to set
     */
    public void setP12Password(String p12Password) {
        this.p12Password = p12Password;
    }


    /**
     * @return the p12Location
     */
    public String getP12Location() {
        return p12Location;
    }


    /**
     * @param p12Location the p12Location to set
     */
    public void setP12Location(String p12Location) {
        this.p12Location = p12Location;
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Person#getFirstName()
     */
    public String getFirstName() {
        return person.getFirstName();
    }


    /**
     * @param firstName
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Person#setFirstName(java.lang.String)
     */
    public void setFirstName(String firstName) {
        person.setFirstName(firstName);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Person#getSsin()
     */
    public String getSsin() {
        return person.getSsin();
    }


    /**
     * @param ssin
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Person#setSsin(java.lang.String)
     */
    public void setSsin(String ssin) {
        person.setSsin(ssin);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Person#getCardnumber()
     */
    public String getCardnumber() {
        return person.getCardnumber();
    }


    /**
     * @param cardnumber
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Person#setCardnumber(java.lang.String)
     */
    public void setCardnumber(String cardnumber) {
        person.setCardnumber(cardnumber);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Person#getLastName()
     */
    public String getLastName() {
        return person.getLastName();
    }


    /**
     * @param lastName
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Person#setLastName(java.lang.String)
     */
    public void setLastName(String lastName) {
        person.setLastName(lastName);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Person#getMut()
     */
    public String getMut() {
        return person.getMut();
    }


    /**
     * @param mut
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Person#setMut(java.lang.String)
     */
    public void setMut(String mut) {
        person.setMut(mut);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Person#getMutualityNumber()
     */
    public String getMutualityNumber() {
        return person.getMutualityNumber();
    }


    /**
     * @param mutualityNumber
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Person#setMutualityNumber(java.lang.String)
     */
    public void setMutualityNumber(String mutualityNumber) {
        person.setMutualityNumber(mutualityNumber);
    }


    /**
     * @return the environmentName
     */
    public String getEnvironmentName() {
        return environmentName;
    }


    /**
     * @param environmentName the environmentName to set
     */
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }


}
