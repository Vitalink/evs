package org.imec.ivlab.viewer.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.imec.ivlab.core.model.internal.mapper.medication.MedicationEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.mapper.MedicationMapper;
import org.imec.ivlab.core.model.internal.parser.childprevention.ChildPrevention;
import org.imec.ivlab.core.model.internal.parser.childprevention.mapper.ChildPreventionMapper;
import org.imec.ivlab.core.model.internal.parser.diarynote.DiaryNote;
import org.imec.ivlab.core.model.internal.parser.diarynote.mapper.DiaryNoteMapper;
import org.imec.ivlab.core.model.internal.parser.populationbasedscreening.PopulationBasedScreening;
import org.imec.ivlab.core.model.internal.parser.populationbasedscreening.mapper.PopulationBasedScreeningMapper;
import org.imec.ivlab.core.model.internal.parser.sumehr.Sumehr;
import org.imec.ivlab.core.model.internal.parser.sumehr.mapper.SumehrMapper;
import org.imec.ivlab.core.model.internal.parser.vaccination.Vaccination;
import org.imec.ivlab.core.model.internal.parser.vaccination.mapper.VaccinationMapper;
import org.imec.ivlab.core.model.upload.KmehrWithReference;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.util.CollectionsUtil;

public class TestFileConverter {


    public static List<Sumehr> convertToSumehrs(KmehrWithReferenceList sumehrList) {

        List<Sumehr> sumehrs = new ArrayList<>();

        if (sumehrList == null || CollectionsUtil.emptyOrNull(sumehrList.getList())) {
            return sumehrs;
        }

        for (KmehrWithReference sumehr : sumehrList.getList()) {
            sumehrs.add(SumehrMapper.kmehrToSumehr(sumehr.getKmehrMessage()));
        }

        return sumehrs;

    }

    public static List<DiaryNote> convertToDiaryNotes(KmehrWithReferenceList diaryNoteList) {

        List<DiaryNote> diaryNotes = new ArrayList<>();

        if (diaryNoteList == null || CollectionsUtil.emptyOrNull(diaryNoteList.getList())) {
            return diaryNotes;
        }

        return diaryNoteList.getList().stream()
            .map(diaryNote -> DiaryNoteMapper.kmehrToDiaryNote(diaryNote.getKmehrMessage()))
            .collect(Collectors.toList());

    }

    public static List<Vaccination> convertToVaccinations(KmehrWithReferenceList vaccinationList) {

        List<Vaccination> vaccinations = new ArrayList<>();

        if (vaccinationList == null || CollectionsUtil.emptyOrNull(vaccinationList.getList())) {
            return vaccinations;
        }

        return vaccinationList.getList().stream()
                            .map(diaryNote -> VaccinationMapper.kmehrToVaccination(diaryNote.getKmehrMessage()))
                            .collect(Collectors.toList());

    }

    public static List<ChildPrevention> convertToChildPreventions(KmehrWithReferenceList childPreventionList) {

        List<ChildPrevention> childPreventions = new ArrayList<>();

        if (childPreventionList == null || CollectionsUtil.emptyOrNull(childPreventionList.getList())) {
            return childPreventions;
        }

        return childPreventionList.getList().stream()
                              .map(childPrevention -> ChildPreventionMapper.kmehrToChildPrevention(childPrevention.getKmehrMessage()))
                              .collect(Collectors.toList());

    }


    public static List<PopulationBasedScreening> convertToPopulationBasedScreenings(KmehrWithReferenceList childPreventionList) {

        List<PopulationBasedScreening> childPreventions = new ArrayList<>();

        if (childPreventionList == null || CollectionsUtil.emptyOrNull(childPreventionList.getList())) {
            return childPreventions;
        }

        return childPreventionList.getList().stream()
                                  .map(childPrevention -> PopulationBasedScreeningMapper.kmehrToPopulationBasedScreening(childPrevention.getKmehrMessage()))
                                  .collect(Collectors.toList());

    }

    public static List<MedicationEntry> convertToMedicationEntries(MSEntryList msEntryList) {

        List<MedicationEntry> medicationEntries = new ArrayList<>();

        if (msEntryList == null || CollectionsUtil.emptyOrNull(msEntryList.getMsEntries())) {
            return medicationEntries;
        }

        for (MSEntry msEntry : msEntryList.getMsEntries()) {
            medicationEntries.add(MedicationMapper.transactionToMedicationEntry(msEntry.getMseTransaction(), msEntry.getTsTransactions()));
        }

        return medicationEntries;

    }

}
