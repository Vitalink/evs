package org.imec.ivlab.core.model.internal.mapper.medication;

public enum Route {

    R00001("00001", "In het oor", true),
    R00002("00002", "Op de huid", true),
    R00003("00003", "", false),
    R00004("00004", "", false),
    R00005("00005", "Inspuiting buiten het hersenvlies", true),
    R00006("00006", "", false),
    R00007("00007", "", false),
    R00008("00008", "In de luchtpijp", true),
    R00009("00009", "Inbrengen in de baarmoederhals 2", true),
    R00010("00010", "Inspuiting in een slagader 2", true),
    R00011("00011", "Inspuiting in de penis 2", true),
    R00012("00012", "Inspuiting in de huid 2", true),
    R00013("00013", "Inspuiting in een spier", true),
    R00014("00014", "", false),
    R00015("00015", "", false),
    R00016("00016", "", false),
    R00017("00017", "", false),
    R00018("00018", "", false),
    R00019("00019", "", false),
    R00020("00020", "", false),
    R00021("00021", "", false),
    R00023("00023", "", false),
    R00024("00024", "", false),
    R00025("00025", "", false),
    R00026("00026", "", false),
    R00027("00027", "", false),
    R00028("00028", "", false),
    R00029("00029", "", false),
    R00030("00030", "", false),
    R00031("00031", "", false),
    R00032("00032", "", false),
    R00033("00033", "Inspuiting in het ruggenmerg", true),
    R00034("00034", "Inbrengen in de baarmoeder", true),
    R00035("00035", "Inspuiting in een ader", true),
    R00036("00036", "", false),
    R00037("00037", "", false),
    R00038("00038", "", false),
    R00039("00039", "", false),
    R00040("00040", "", false),
    R00041("00041", "", false),
    R00042("00042", "", false),
    R00043("00043", "", false),
    R00044("00044", "", false),
    R00045("00045", "In de urineblaas", true),
    R00046("00046", "Infuus", true),
    R00047("00047", "", false),
    R00048("00048", "", false),
    R00049("00049", "Inhalatie", true),
    R00050("00050", "", false),
    R00051("00051", "Inspuiting", true),
    R00052("00052", "Op de lippen", true),
    R00053("00053", "Lokaal", true),
    R00054("00054", "Op het slijmvlies", true),
    R00055("00055", "In de neus", true),
    R00056("00056", "In de ogen", true),
    R00057("00057", "", false),
    R00058("00058", "", false),
    R00059("00059", "", false),
    R00060("00060", "Innemen", true),
    R00061("00061", "", false),
    R00062("00062", "", false),
    R00063("00063", "", false),
    R00064("00064", "In de keel", true),
    R00065("00065", "", false),
    R00066("00066", "In de keel en in de neus", true),
    R00067("00067", "Anaal opsteken", true),
    R00068("00068", "Onderhuidse inspuiting", true),
    R00069("00069", "", false),
    R00070("00070", "Onder de tong", true),
    R00071("00071", "Op de tanden", true),
    R00072("00072", "In de urineleider", true),
    R00073("00073", "Vaginaal", true),
    UNSUPPORTED_ROUTE("", "UNSUPPORTED_ROUTE", false);

    private String code;
    private String patientTranslation;
    private boolean supportedByVitalink;

    Route(String code, String patientTranslation, boolean supportedByVitalink) {
        this.code = code;
        this.patientTranslation = patientTranslation;
        this.supportedByVitalink = supportedByVitalink;
    }

    public String getValue() {
        return code;
    }

    public String getPatientTranslation() {
        return patientTranslation;
    }

    public static Route fromCode(String input) {
        for (Route route : values()) {
            if (route.code.equalsIgnoreCase(input)) {
                return route;
            }
        }

        throw new RuntimeException("Route code is not valid: " + input);
    }

    public boolean isSupportedByVitalink() {
        return supportedByVitalink;
    }
}
