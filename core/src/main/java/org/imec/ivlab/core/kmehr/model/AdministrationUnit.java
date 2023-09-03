package org.imec.ivlab.core.kmehr.model;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public enum AdministrationUnit {

    U_00001("00001", "5ml", "Koffielepel (5 ml)", true),
    U_00002("00002", "amp.", "ampule", true),
    U_00003("00003", "applic.", "aanbrengen", true),
    U_00004("00004", "caps.", "Capsule", true),
    U_00005("00005", "compr.", "Tablet", true),
    U_00006("00006", "Dosis", "Dosis", true),
    U_00007("00007", "Druppels", "Druppel(s)", true),
    U_00008("00008", "flac.", "Flesje", true),
    U_00009("00009", "Implant", "Implantaat", true),
    U_00010("00010", "Infuus", "Infuus", true),
    U_00011("00011", "inhal.", "Inhalatie", true),
    U_00012("00012", "Insert", "Inbrengen", true),
    U_00013("00013", "Kauwgom", "Kauwgom", true),
    U_00014("00014", "kompres(sen", "Kompres(sen)", true),
    U_00015("00015", "lav.", "Lavement", true),
    U_00016("00016", "Ml", "ml", true),
    U_00017("00017", "ov.", "Ovule", true),
    U_00018("00018", "parel(s)", "Parel", true),
    U_00019("00019", "past.", "Pastille", true),
    U_00020("00020", "Patch", "Pleister", true),
    U_00021("00021", "patr.", "Patroon", true),
    U_00022("00022", "Pen", "Pen", true),
    U_00023("00023", "puff(s)", "Puff(s)", true),
    U_00024("00024", "Spons", "Spons", true),
    U_00025("00025", "Stylo", "", false),
    U_00026("00026", "Suppo", "Zetpil", true),
    U_00027("00027", "Tube", "Tube", true),
    U_00028("00028", "Wiek", "Wiek", true),
    U_00029("00029", "Zak", "Zak", true),
    U_00030("00030", "zakje(s)", "Zakje", true),
    CM("cm", "Centimeter", "Centimeter", true),
    DROPSPERMINUTE("dropsperminute", "Druppels per minuut", "Druppels per minuut", true),
    GM("gm", "Gram", "Gram", true),
    INTERNATIONALUNITS("internationalunits", "Internationale eenheden", "Internationale eenheden", true),
    MCK_H("mck/h", "Microgram per uur", "Microgram per uur", true),
    MCK_KG_MINUTE("mck/kg/minute", "Microgram per kilogram per minuut", "Microgram per kilogram per minuut", true),
    MEASURE("measure", "Maat", "Maat", true),
    MG_H("mg/h", "Milligram per uur", "Milligram per uur", true),
    ML_H("ml/h", "Milliliter per uur", "Milliliter per uur", true),
    TBL("tbl", "Eetlepel", "Eetlepel", true),
    TSP("tsp", "Koffielepel", "Koffielepel", true),
    UNT_H("unt/h", "Eenheden per uur", "Eenheden per uur", true),
    MG("mg", "Milligram", "Milligram", true),
    MG_ML("mg/ml", "Milligram per milliliter", "Milligram per milliliter", true);

    //private static Logger log = LogManager.getLogger(AdministrationUnit.class);
    private String value;
    private String patientTranslation;
    private String descriptionDutch;
    private boolean supportedByVitalink;

    AdministrationUnit(String value, String descriptionNL, String translation, boolean supportedByVitalink) {
        this.value = value;
        this.patientTranslation = translation;
        this.descriptionDutch = descriptionNL;
        this.supportedByVitalink = supportedByVitalink;
    }

    public String getPatientTranslation() {
        return patientTranslation;
    }

    public String getValue() {
        return value;
    }

    public String getDescriptionDutch() {
        return descriptionDutch;
    }

    public boolean isSupportedByVitalink() {
        return supportedByVitalink;
    }

    public static AdministrationUnit fromValue(String input) {
        for (AdministrationUnit administrationUnit : values()) {
            if (administrationUnit.value.equalsIgnoreCase(input)) {
                return administrationUnit;
            }
        }

        throw new IllegalArgumentException("Invalid administrationunit value: " + input);

    }

}
