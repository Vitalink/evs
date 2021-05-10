package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDPERIODICITY;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.Frequency;
import org.imec.ivlab.core.kmehr.model.FrequencyCode;
import org.imec.ivlab.core.kmehr.model.RegimenEntry;
import org.imec.ivlab.core.kmehr.model.util.FrequencyUtil;
import org.imec.ivlab.core.kmehr.model.util.RegimenUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class R1002_UniqueTakingTimes extends BaseMSEntryRule implements MSEntryRule {

    @Override
    public String getMessage() {
        return "The combination of daytime (dayperiod/time) and daynumber/date/weekday must be unique. If no daynumber/date/weekday is specified, the daytime must be unique itself";
    }

    @Override
    public String getRuleId() {
        return "1002";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        ItemType medicationItem = TransactionUtil.getItem(msEntry.getMseTransaction(), CDITEMvalues.MEDICATION);

        Set<String> times = new HashSet<>();
        Set<String> dayPeriodValues = new HashSet<>();

        List<RegimenEntry> regimenEntries = RegimenUtil.getRegimenEntries(medicationItem.getRegimen());

        CDPERIODICITY dayPeriod = FrequencyUtil.getDayPeriod(medicationItem.getFrequency());
        FrequencyCode frequencyCode = null;
        if (dayPeriod != null) {
            frequencyCode = FrequencyCode.fromValue(dayPeriod.getValue());
        }

        if (CollectionsUtil.notEmptyOrNull(regimenEntries)) {

            for (RegimenEntry regimenEntry : regimenEntries) {

                if (regimenEntry.getDaytime() == null) {
                    continue;
                }

                String dateDaynumberWeekday = getDateDaynumberWeekdayOrEmptyString(regimenEntry, frequencyCode) + "-";

                if (regimenEntry.getDaytime().getTime() != null) {
                    LocalTime localTime = regimenEntry.getDaytime().getTime().getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

                    String timeString = dateDaynumberWeekday + StringUtils.joinWith(":", localTime.getHour(), localTime.getMinute());
                    if (times.contains(timeString)) {
                        return failRule(msEntry.getMseTransaction());
                    } else {
                        times.add(timeString);
                    }
                }

                if (regimenEntry.getDaytime().getDayperiod() != null && regimenEntry.getDaytime().getDayperiod().getCd() != null && regimenEntry.getDaytime().getDayperiod().getCd().getValue() != null) {
                    String dayperiodString = dateDaynumberWeekday + regimenEntry.getDaytime().getDayperiod().getCd().getValue();
                    if (dayPeriodValues.contains(dayperiodString)) {
                        return failRule(msEntry.getMseTransaction());
                    } else {
                        dayPeriodValues.add(dayperiodString);
                    }
                }

            }


        }

        return passRule();
    }

    private String getDateDaynumberWeekdayOrEmptyString(RegimenEntry regimenEntry, FrequencyCode frequencyCode) {

        if (regimenEntry.getDayNumber() != null) {
            return regimenEntry.getDayNumber().toString();
        }

        if (regimenEntry.getWeekday() != null && regimenEntry.getWeekday().getCd() != null && regimenEntry.getWeekday().getCd().getValue() != null) {
            return regimenEntry.getWeekday().getCd().getValue().toString();
        }

        if (regimenEntry.getDate() != null) {
            LocalDate localDate = regimenEntry.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (frequencyCode != null && frequencyCode.getFrequency().equals(Frequency.MONTH)) {
                return String.valueOf(localDate.getDayOfMonth());
            }

            if (frequencyCode != null  && frequencyCode.getFrequency().equals(Frequency.YEAR)) {
                return String.valueOf(localDate.getDayOfMonth()) + "-" + String.valueOf(localDate.getMonthValue());
            }

            return String.valueOf(localDate.getDayOfMonth()) + "-" + String.valueOf(localDate.getMonthValue()) + "-" + String.valueOf(localDate.getYear());

        }

        return "";

    }


}
