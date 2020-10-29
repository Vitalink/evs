/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.testperson.domain;


/**
 * Information for a profession of a person.
 * 
 * @author EHP
 */
public class TestPerson {

    private String professionType;

    private String professionNihii;

    private Environment env;


    /**
     * @param env
     */
    public TestPerson(Environment env) {
        super();
        this.env = env;
    }


    /**
     * @return the professionType
     */
    public String getProfessionType() {
        return professionType;
    }


    /**
     * @param professionType the professionType to set
     */
    public void setProfessionType(String professionType) {
        this.professionType = professionType;
    }


    /**
     * @return the professionNihii
     */
    public String getProfessionNihii() {
        return professionNihii;
    }


    /**
     * @param professionNihii the professionNihii to set
     */
    public void setProfessionNihii(String professionNihii) {
        this.professionNihii = professionNihii;
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#getP12Password()
     */
    public String getP12Password() {
        return env.getP12Password();
    }


    /**
     * @param p12Password
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#setP12Password(java.lang.String)
     */
    public void setP12Password(String p12Password) {
        env.setP12Password(p12Password);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#getP12Location()
     */
    public String getP12Location() {
        return env.getP12Location();
    }


    /**
     * @param p12Location
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#setP12Location(java.lang.String)
     */
    public void setP12Location(String p12Location) {
        env.setP12Location(p12Location);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#getFirstName()
     */
    public String getFirstName() {
        return env.getFirstName();
    }


    /**
     * @param firstName
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#setFirstName(java.lang.String)
     */
    public void setFirstName(String firstName) {
        env.setFirstName(firstName);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#getSsin()
     */
    public String getSsin() {
        return env.getSsin();
    }


    /**
     * @param ssin
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#setSsin(java.lang.String)
     */
    public void setSsin(String ssin) {
        env.setSsin(ssin);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#getCardnumber()
     */
    public String getCardnumber() {
        return env.getCardnumber();
    }


    /**
     * @param cardnumber
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#setCardnumber(java.lang.String)
     */
    public void setCardnumber(String cardnumber) {
        env.setCardnumber(cardnumber);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#getLastName()
     */
    public String getLastName() {
        return env.getLastName();
    }


    /**
     * @param lastName
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#setLastName(java.lang.String)
     */
    public void setLastName(String lastName) {
        env.setLastName(lastName);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#getMut()
     */
    public String getMut() {
        return env.getMut();
    }


    /**
     * @param mut
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#setMut(java.lang.String)
     */
    public void setMut(String mut) {
        env.setMut(mut);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#getMutualityNumber()
     */
    public String getMutualityNumber() {
        return env.getMutualityNumber();
    }


    /**
     * @param mutualityNumber
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#setMutualityNumber(java.lang.String)
     */
    public void setMutualityNumber(String mutualityNumber) {
        env.setMutualityNumber(mutualityNumber);
    }


    /**
     * @return
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#getEnvironmentName()
     */
    public String getEnvironmentName() {
        return env.getEnvironmentName();
    }


    /**
     * @param environmentName
     * @see be.ehealth.businessconnector.testcommons.testperson.domain.Environment#setEnvironmentName(java.lang.String)
     */
    public void setEnvironmentName(String environmentName) {
        env.setEnvironmentName(environmentName);
    }


}
