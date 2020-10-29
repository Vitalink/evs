package org.imec.ivlab.core.kmehr.model;

import java.io.Serializable;
import java.util.Objects;

public enum FrequencyCode implements Serializable {

        UH("UH", Frequency.HOUR, 0.5, "Om het half uur", true),
        U("U", Frequency.HOUR, 1, "Om het uur", true),
        UT("UT", Frequency.HOUR, 2, "Om de twee uren", true),
        UD("UD", Frequency.HOUR, 3, "Om de 3 uren", true),
        UV("UV", Frequency.HOUR, 4, "om de 4 uren", true),
        UQ("UQ", Frequency.HOUR, 5, "om de 5 uren", true), // niet toegelaten in medicatieschema
        UZ("UZ", Frequency.HOUR, 6, "om de 6 uren", true),
        US("US", Frequency.HOUR, 7, "om de 7 uren", true), // niet toegelaten in medicatieschema
        UA("UA", Frequency.HOUR, 8, "om de 8 uren", true),
        UN("UN", Frequency.HOUR, 9, "om de 9 uren", true), // niet toegelaten in medicatieschema
        UX("UX", Frequency.HOUR, 10, "om de 10 uren", true),// niet toegelaten in medicatieschema
        UE("UE", Frequency.HOUR, 11, "om de 11 uren", true),// niet toegelaten in medicatieschema
        UW("UW", Frequency.HOUR, 12, "om de 12 uren", true),
        D("D", Frequency.DAY, 1, "Dagelijks", true),
        O1("O1", Frequency.DAY, 2, "Om de 2 dagen", true), // TODO: uit te klaren. Volgens kmehr: om de dag, volgens medicatie schema cookbook om de 2 dagen
        DT("DT", Frequency.DAY, 2, "om de 2 dagen", true),
        DD("DD", Frequency.DAY, 3, "Om de 3 dagen", true),
        DV("DV", Frequency.DAY, 4, "Om de 4 dagen", true),
        DQ("DQ", Frequency.DAY, 5, "Om de 5 dagen", true),
        DZ("DZ", Frequency.DAY, 6, "Om de 6 dagen", true),
        DA("DA", Frequency.DAY, 8, "Om de 8 dagen", true),
        DN("DN", Frequency.DAY, 9, "Om de 9 dagen", true),
        DX("DX", Frequency.DAY, 10, "Om de 10 dagen", true),
        DE("DE", Frequency.DAY, 11, "Om de 11 dagen", true),
        DW("DW", Frequency.DAY, 12, "Om de 12 dagen", true),
        W("W", Frequency.WEEK, 1, "Wekelijks", true),
        WT("WT", Frequency.WEEK, 2, "Om de 2 weken", true),
        WD("WD", Frequency.WEEK, 3, "Om de 3 weken", true),
        WV("WV", Frequency.WEEK, 4, "Om de 4 weken", true),
        WQ("WQ", Frequency.WEEK, 5, "Om de 5 weken", true),
        WZ("WZ", Frequency.WEEK, 6, "Om de 6 weken", true),
        WS("WS", Frequency.WEEK, 7, "Om de 7 weken", true),
        WA("WA", Frequency.WEEK, 8, "Om de 8 weken", true),
        WN("WN", Frequency.WEEK, 9, "Om de 9 weken", true),
        WX("WX", Frequency.WEEK, 10, "Om de 10 weken", true),
        WE("WE", Frequency.WEEK, 11, "Om de 11 weken", true),
        WW("WW", Frequency.WEEK, 12, "Om de 12 weken", true),
        WP("WP", Frequency.WEEK, 24, "Om de 24 weken", true),
        M("M", Frequency.MONTH, 1, "Maandelijks", true),
        MT("MT", Frequency.MONTH, 2, "Om de 2 maanden", true),
        MD("MD", Frequency.MONTH, 3, "Om de 3 maanden", true),
        MV("MV", Frequency.MONTH, 4, "Om de 4 maanden", true),
        MQ("MQ", Frequency.MONTH, 5, "Om de 5 maanden", true),
        MZ2("MZ2", Frequency.MONTH, 6, "Om de 6 maanden", true),
        MS("MS", Frequency.MONTH, 7, "Om de 7 maanden", true),
        MA("MA", Frequency.MONTH, 8, "Om de 8 maanden", true),
        MN("MN", Frequency.MONTH, 9, "Om de 9 maanden", true),
        MX("MX", Frequency.MONTH, 10, "Om de 10 maanden", true),
        ME("ME", Frequency.MONTH, 11, "Om de 11 maanden", true),
        MC("MC", Frequency.MONTH, 18, "Om de 18 maanden", true),
        JH2("JH2", Frequency.MONTH, 6, "Halfjaarlijks", true), // YEAR, 0.5 in specs
        J("J", Frequency.YEAR, 1, "Jaarlijks", true),
        JT("JT", Frequency.YEAR, 2, "Om de 2 jaar", true),
        JD("JD", Frequency.YEAR, 3, "Om de 3 jaar", true),
        JV("JV", Frequency.YEAR, 4, "Om de 4 jaar", true),
        JQ("JQ", Frequency.YEAR, 5, "Om de 5 jaar", true),
        JZ("JZ", Frequency.YEAR, 6, "Om de 6 jaar", true),
        ONDEMAND("ondemand", null, 0, "Op aanvraag", false);

        private Frequency frequency;
        private Double multiplier;
        private String translation;
        private boolean supportedByVitalink;
        private String value;


        FrequencyCode(String value, Frequency frequency, int multiplier, String translation, boolean supportedByVitalink) {
                this(value, frequency, Double.valueOf(multiplier), translation, supportedByVitalink);
        }

        FrequencyCode(String value, Frequency frequency, Double multiplier, String translation, boolean supportedByVitalink) {
                this.value = value;
                this.frequency = frequency;
                this.multiplier = multiplier;
                this.translation = translation;
                this.supportedByVitalink = supportedByVitalink;
        }

        public Double getMultiplier() {
            return multiplier;
        }

        public Frequency getFrequency() {
            return frequency;
        }

        public String getTranslation() {
                return translation;
        }

        public boolean hasFrequency(Frequency frequency) {
            return Objects.equals(this.frequency, frequency);
        }

        public boolean isSupportedByVitalink() {
                return supportedByVitalink;
        }

        public static FrequencyCode fromValue(String input) {
                for (FrequencyCode frequencyCode : values()) {
                        if (frequencyCode.value.equalsIgnoreCase(input)) {
                                return frequencyCode;
                        }
                }

                throw new IllegalArgumentException("Invalid frequencycode value: " + input);

        }

        public String getValue() {
                return value;
        }
}