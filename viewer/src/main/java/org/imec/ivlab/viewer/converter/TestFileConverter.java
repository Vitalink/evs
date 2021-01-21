package org.imec.ivlab.viewer.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.imec.ivlab.core.model.internal.mapper.medication.MedicationEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.mapper.MedicationMapper;
import org.imec.ivlab.core.model.internal.parser.diarynote.DiaryNote;
import org.imec.ivlab.core.model.internal.parser.diarynote.mapper.DiaryNoteMapper;
import org.imec.ivlab.core.model.internal.parser.sumehr.Sumehr;
import org.imec.ivlab.core.model.internal.parser.sumehr.mapper.SumehrMapper;
import org.imec.ivlab.core.model.internal.parser.vaccination.Vaccination;
import org.imec.ivlab.core.model.internal.parser.vaccination.mapper.VaccinationMapper;
import org.imec.ivlab.core.model.upload.diarylist.DiaryNoteList;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.model.upload.sumehrlist.SumehrList;
import org.imec.ivlab.core.model.upload.vaccinationentry.VaccinationList;
import org.imec.ivlab.core.util.CollectionsUtil;

public class TestFileConverter {


    public static List<Sumehr> convertToSumehrs(SumehrList sumehrList) {

        List<Sumehr> sumehrs = new ArrayList<>();

        if (sumehrList == null || CollectionsUtil.emptyOrNull(sumehrList.getList())) {
            return sumehrs;
        }

        for (org.imec.ivlab.core.model.upload.sumehrlist.Sumehr sumehr : sumehrList.getList()) {
            sumehrs.add(SumehrMapper.kmehrToSumehr(sumehr.getKmehrMessage()));
        }

        return sumehrs;

    }

    public static List<DiaryNote> convertToDiaryNotes(DiaryNoteList diaryNoteList) {

        List<DiaryNote> diaryNotes = new ArrayList<>();

        if (diaryNoteList == null || CollectionsUtil.emptyOrNull(diaryNoteList.getList())) {
            return diaryNotes;
        }

        return diaryNoteList.getList().stream()
            .map(diaryNote -> DiaryNoteMapper.kmehrToDiaryNote(diaryNote.getKmehrMessage()))
            .collect(Collectors.toList());

    }

    public static List<Vaccination> convertTVaccinations(VaccinationList vaccinationList) {

        List<Vaccination> vaccinations = new ArrayList<>();

        if (vaccinationList == null || CollectionsUtil.emptyOrNull(vaccinationList.getList())) {
            return vaccinations;
        }

        return vaccinationList.getList().stream()
                            .map(diaryNote -> VaccinationMapper.kmehrToVaccination(diaryNote.getKmehrMessage()))
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
