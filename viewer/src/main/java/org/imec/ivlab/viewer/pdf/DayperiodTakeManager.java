package org.imec.ivlab.viewer.pdf;

import org.imec.ivlab.core.model.internal.mapper.medication.Dayperiod;

import java.util.LinkedHashSet;
import java.util.Set;

public class DayperiodTakeManager {

    private Set<Dayperiod> allPossibleStandaloneDayperiods = new LinkedHashSet<>();

    public DayperiodTakeManager() {

        allPossibleStandaloneDayperiods.add(Dayperiod.MORNING);
        allPossibleStandaloneDayperiods.add(Dayperiod.BEFOREBREAKFAST);
        allPossibleStandaloneDayperiods.add(Dayperiod.DURINGBREAKFAST);
        allPossibleStandaloneDayperiods.add(Dayperiod.AFTERBREAKFAST);
        allPossibleStandaloneDayperiods.add(Dayperiod.BEFORELUNCH);
        allPossibleStandaloneDayperiods.add(Dayperiod.DURINGLUNCH);
        allPossibleStandaloneDayperiods.add(Dayperiod.AFTERLUNCH);
        allPossibleStandaloneDayperiods.add(Dayperiod.BEFOREDINNER);
        allPossibleStandaloneDayperiods.add(Dayperiod.DURINGDINNER);
        allPossibleStandaloneDayperiods.add(Dayperiod.AFTERDINNER);
        allPossibleStandaloneDayperiods.add(Dayperiod.THEHOUROFSLEEP);

    }

    public boolean isStandaloneDayperiod(Dayperiod dayperiod) {
        return allPossibleStandaloneDayperiods.contains(dayperiod);
    }

    public Set<Dayperiod> getAllPossibleStandaloneDayperiods() {
        return allPossibleStandaloneDayperiods;
    }


}
