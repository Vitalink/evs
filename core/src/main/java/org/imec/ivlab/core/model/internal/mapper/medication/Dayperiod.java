package org.imec.ivlab.core.model.internal.mapper.medication;

public enum Dayperiod {

    AFTERBREAKFAST("afterbreakfast"),
    AFTERDINNER("afterdinner"),
    AFTERLUNCH("afterlunch"),
    AFTERMEAL("aftermeal"),
    AFTERNOON("afternoon"),
    BEFOREBREAKFAST("beforebreakfast"),
    BEFOREDINNER("beforedinner"),
    BEFORELUNCH("beforelunch"),
    BETWEENBREAKFASTANDLUNCH("betweenbreakfastandlunch"),
    BETWEENDINNERANDSLEEP("betweendinnerandsleep"),
    BETWEENLUNCHANDDINNER("betweenlunchanddinner"),
    BETWEENMEALS("betweenmeals"),
    EVENING("evening"),
    MORNING("morning"),
    NIGHT("night"),
    THEHOUROFSLEEP("thehourofsleep"),
    DURINGBREAKFAST("duringbreakfast"),
    DURINGLUNCH("duringlunch"),
    DURINGDINNER("duringdinner");

    private String value;

    Dayperiod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Dayperiod fromValue(String input) {
        for (Dayperiod dayperiod : values()) {
            if (dayperiod.value.equalsIgnoreCase(input)) {
                return dayperiod;
            }
        }

        throw new IllegalArgumentException("Dayperiod is not valid: " + input);
    }

}
