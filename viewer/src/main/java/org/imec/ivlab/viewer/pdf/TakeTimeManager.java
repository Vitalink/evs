package org.imec.ivlab.viewer.pdf;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenTime;

public class TakeTimeManager {

    protected static final int MAX_NUMBER_OF_STANDALONE_TAKING_TIMES = 3;

    private Set<String> takeTimes = new TreeSet<>();

    public TakeTimeManager(List<RegimenEntry> regimenEntries) {
        convertToTakeTimes(regimenEntries);
    }

    private void convertToTakeTimes(List<RegimenEntry> regimenEntries) {


        if (regimenEntries == null) {
            return;
        }

        for (RegimenEntry regimenEntry : regimenEntries) {
            if (regimenEntry.getDayperiodOrTime() instanceof RegimenTime) {
                RegimenTime regimenTime = (RegimenTime) regimenEntry.getDayperiodOrTime();
                if (regimenTime == null) {
                    continue;
                }

                String timeString = toTakeTimeString(regimenTime.getTime());
                if (!takeTimes.contains(timeString)) {
                    takeTimes.add(timeString);
                }
            }
        }


    }

    public String toTakeTimeString(java.time.LocalTime localTime) {
        return StringUtils.joinWith("u", localTime.format(java.time.format.DateTimeFormatter.ofPattern("HH")), localTime.format(java.time.format.DateTimeFormatter.ofPattern("mm")));
    }

    public String toTakeTimeString(org.joda.time.LocalTime localTime) {
        return String.format(StringUtils.joinWith("u", org.joda.time.format.DateTimeFormat.fullTime().print(localTime)));
    }

    public String toTakeTimeString(org.joda.time.DateTime localTime) {
        return String.format(StringUtils.joinWith("u", org.joda.time.format.DateTimeFormat.fullTime().print(localTime)));
    }

    public Set<String> getTakeTimes() {
        return takeTimes;
    }


    public boolean isStandaloneTakeTime(String taketime) {
        return getStandaloneTakeTimes().contains(taketime);
    }

    public Set<String> getStandaloneTakeTimes() {
        int timeColumnsWithValues = Math.min(MAX_NUMBER_OF_STANDALONE_TAKING_TIMES, takeTimes.size());
        List<String> takeTimesSubset = new ArrayList<String>(takeTimes).subList(0, timeColumnsWithValues);
        Set<String> standaloneTakeTimes = new TreeSet<>();
        standaloneTakeTimes.addAll(takeTimesSubset);
        return standaloneTakeTimes;
    }

}