package org.imec.ivlab.core.model.internal.mapper.medication;

public enum TimeUnit {

    A("a", "jaar", "jaar"),
    MO("mo", "maand", "maanden"),
    WK("wk", "week", "weken"),
    D("d", "dag", "dagen"),
    HR("hr", "uur", "uren"),
    MIN("min", "minuut", "minuten"),
    S("s", "seconde", "seconden"),
    MS("ms", "milliseconde", "milliseconden"),
    US("us", "microseconde", "microseconden"),
    NS("ns", "nanoseconde", "nanoseconden");

    private String value;
    private String valueForSingle;
    private String valueForMultiple;

    TimeUnit(String value, String valueForSingle, String valueForMultiple) {
        this.value = value;
        this.valueForSingle = valueForSingle;
        this.valueForMultiple = valueForMultiple;
    }

    public String getValue() {
        return value;
    }

    public String getValueForSingle() {
        return valueForSingle;
    }

    public String getValueForMultiple() {
        return valueForMultiple;
    }

    public static TimeUnit fromValue(String input) {
        for (TimeUnit timeUnit : values()) {
            if (timeUnit.value.equalsIgnoreCase(input)) {
                return timeUnit;
            }
        }

        throw new IllegalArgumentException("Timeunit is not valid: " + input);
    }

}
