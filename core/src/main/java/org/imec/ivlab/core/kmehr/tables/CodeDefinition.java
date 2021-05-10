package org.imec.ivlab.core.kmehr.tables;

public class CodeDefinition {

    private String code;
    private String meaningEnglish;
    private String meaningDutch;
    private String meaningFrench;
    private String meaningGerman;
    private String snomedCt;
    private long definitionNumber;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMeaningEnglish() {
        return meaningEnglish;
    }

    public void setMeaningEnglish(String meaningEnglish) {
        this.meaningEnglish = meaningEnglish;
    }

    public String getMeaningDutch() {
        return meaningDutch;
    }

    public void setMeaningDutch(String meaningDutch) {
        this.meaningDutch = meaningDutch;
    }

    public String getMeaningFrench() {
        return meaningFrench;
    }

    public void setMeaningFrench(String meaningFrench) {
        this.meaningFrench = meaningFrench;
    }

    public String getMeaningGerman() {
        return meaningGerman;
    }

    public void setMeaningGerman(String meaningGerman) {
        this.meaningGerman = meaningGerman;
    }

    public String getSnomedCt() {
        return snomedCt;
    }

    public void setSnomedCt(String snomedCt) {
        this.snomedCt = snomedCt;
    }

    public long getDefinitionNumber() {
        return definitionNumber;
    }

    public void setDefinitionNumber(long definitionNumber) {
        this.definitionNumber = definitionNumber;
    }
}
