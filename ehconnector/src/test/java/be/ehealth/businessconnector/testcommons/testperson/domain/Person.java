/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.testperson.domain;


/**
 * Information of a person.
 * 
 * @author EHP
 */
public class Person {

    private String ssin;

    private String cardnumber;

    private String firstName;

    private String lastName;

    private String mut;

    private String mutualityNumber;

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    /**
     * @return the ssin
     */
    public String getSsin() {
        return ssin;
    }


    /**
     * @param ssin the ssin to set
     */
    public void setSsin(String ssin) {
        this.ssin = ssin;
    }


    /**
     * @return the cardnumber
     */
    public String getCardnumber() {
        return cardnumber;
    }


    /**
     * @param cardnumber the cardnumber to set
     */
    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }


    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }


    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * @return the mut
     */
    public String getMut() {
        return mut;
    }


    /**
     * @param mut the mut to set
     */
    public void setMut(String mut) {
        this.mut = mut;
    }


    /**
     * @return the mutualityNumber
     */
    public String getMutualityNumber() {
        return mutualityNumber;
    }


    /**
     * @param mutualityNumber the mutualityNumber to set
     */
    public void setMutualityNumber(String mutualityNumber) {
        this.mutualityNumber = mutualityNumber;
    }
}
