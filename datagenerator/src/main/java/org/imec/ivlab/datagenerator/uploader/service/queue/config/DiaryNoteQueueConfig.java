package org.imec.ivlab.datagenerator.uploader.service.queue.config;

import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;
import org.imec.ivlab.core.model.upload.extractor.DiaryNoteListExtractor;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;
import org.imec.ivlab.datagenerator.uploader.model.instruction.DiaryNoteInstruction;
import org.imec.ivlab.datagenerator.uploader.service.DiaryNoteUploaderImpl;


public class DiaryNoteQueueConfig implements QueueConfig<DiaryNoteInstruction> {

    private DiaryNoteUploaderImpl uploader;
    private static DiaryNoteQueueConfig instance = null;

    private DiaryNoteQueueConfig() throws VitalinkException {
        this.uploader = new DiaryNoteUploaderImpl();
    }

    public static DiaryNoteQueueConfig getInstance() throws VitalinkException {

        if(instance == null) {
            instance = new DiaryNoteQueueConfig();
        }
        return instance;

    }

    @Override
    public void configureUploader(DiaryNoteInstruction instruction) {
        // nothing to configure in case of SUMEHR
    }

    @Override
    public void callUploadAction(DiaryNoteInstruction instruction, KmehrEntryList kmehrEntryList) throws UploaderException, VitalinkException {

        KmehrWithReferenceList diaryNoteList = new DiaryNoteListExtractor().getKmehrWithReferenceList(kmehrEntryList);

        switch (instruction.getAction()) {

            case ADD:
                uploader.add(instruction.getPatient(), diaryNoteList, instruction.getActorID());
                break;
            case GENERATE_REF:
                uploader.generateREF(instruction.getPatient(), instruction.getActorID());
                break;
            case UPDATE_REF:
                uploader.updateREF(instruction.getPatient(), diaryNoteList, instruction.getActorID());
                break;
            case EXPORT:
                // do nothing, ugly hack to not upload anything here, since only the callbacks of which 'export' is one of them needs to be executed.
                break;
            default:
                throw new UploaderException("Unsupported instruction action: " + instruction.getAction());

        }

    }


}
