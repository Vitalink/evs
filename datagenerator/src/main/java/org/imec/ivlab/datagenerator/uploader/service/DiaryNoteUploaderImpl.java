package org.imec.ivlab.datagenerator.uploader.service;

import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import java.util.List;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;
import org.imec.ivlab.core.authentication.model.AuthenticationConfig;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.kmehr.KmehrHelper;
import org.imec.ivlab.core.kmehr.model.localid.LocalIdParser;
import org.imec.ivlab.core.kmehr.model.localid.URI;
import org.imec.ivlab.core.kmehr.model.localid.util.URIBuilder;
import org.imec.ivlab.core.kmehr.model.localid.util.URIConverter;
import org.imec.ivlab.core.kmehr.model.util.IDKmehrUtil;
import org.imec.ivlab.core.model.evsref.extractor.impl.DiaryNoteEVSRefExtractor;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.model.upload.KmehrWithReference;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;
import org.imec.ivlab.core.model.upload.msentrylist.exception.IdenticalEVSRefsFoundException;
import org.imec.ivlab.core.model.upload.msentrylist.exception.MultipleEVSRefsInTransactionFoundException;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;
//import org.imec.ivlab.ehconnector.business.HubHelper;
import org.imec.ivlab.ehconnector.business.diary.DiaryNoteService;
import org.imec.ivlab.ehconnector.business.diary.DiaryNoteServiceImpl;

public class DiaryNoteUploaderImpl implements DiaryNoteUploader, Uploader {

    private final static Logger LOG = LogManager.getLogger(DiaryNoteUploaderImpl.class);

    private DiaryNoteService diareNoteService;
    //private HubHelper hubHelper = new HubHelper();
    private DiaryNoteEVSRefExtractor refExtractor;

    public DiaryNoteUploaderImpl() throws VitalinkException {
        this.diareNoteService = new DiaryNoteServiceImpl();
        this.refExtractor = new DiaryNoteEVSRefExtractor();
    }

    @Override
    public void add(Patient patient, KmehrWithReferenceList diaryNoteListToAdd, String actorId) throws UploaderException, VitalinkException {

        try {
            refExtractor.extractEVSRefs(diaryNoteListToAdd);
        } catch (MultipleEVSRefsInTransactionFoundException e) {
            throw new UploaderException(e);
        }

        if (CollectionsUtil.emptyOrNull(diaryNoteListToAdd.getList())) {
            LOG.info("No diarynotes provided for upload");
            return;
        } else {
            LOG.info("About to add " + CollectionsUtil.size(diaryNoteListToAdd.getList()) + " diarynotes to the vault for patientID: " + patient.getId());
        }

        diareNoteService.authenticate(readAuthenticationConfig(actorId));

        KmehrWithReferenceList diareNoteListInVault = diareNoteService.getKmehrWithReferenceList(patient);

        try {
            refExtractor.extractEVSRefs(diareNoteListInVault);
            refExtractor.validateEVSRefUniqueness(refExtractor.merge(diaryNoteListToAdd, diareNoteListInVault));

            refExtractor.generateEVSRefsIfMissing(diaryNoteListToAdd, diareNoteListInVault);

            KmehrHelper.removeLocalIds(diaryNoteListToAdd);
            diareNoteService.putTransactions(patient, diaryNoteListToAdd);

        } catch (Exception e) {
            throw new VitalinkException(e);
        }

    }

    @Override
    public void generateREF(Patient patient, String actorId) throws VitalinkException, UploaderException {

        diareNoteService.authenticate(readAuthenticationConfig(actorId));

        KmehrWithReferenceList diaryNoteListVaultBeforeGeneratingRefs = diareNoteService.getKmehrWithReferenceList(patient);
        KmehrWithReferenceList diaryNoteListVaultWithRefs = SerializationUtils.clone(diaryNoteListVaultBeforeGeneratingRefs);
        KmehrWithReferenceList diaryNoteListVaultWithNewlyGeneratedRefs = SerializationUtils.clone(diaryNoteListVaultWithRefs);
        diaryNoteListVaultWithNewlyGeneratedRefs.getIdentifiables().clear();

        if (CollectionsUtil.emptyOrNull(diaryNoteListVaultBeforeGeneratingRefs.getList())) {
            LOG.info("No diarynotes in vault; No need to generate REFs.");
            return;
        } else {
            LOG.info("About to generate REFs for " + CollectionsUtil.size(diaryNoteListVaultBeforeGeneratingRefs.getList()) + " diarynotes in the vault for patientID: " + patient.getId());
        }

        try {
            refExtractor.extractEVSRefs(diaryNoteListVaultWithRefs);
            refExtractor.validateEVSRefUniqueness(diaryNoteListVaultWithRefs);
            refExtractor.generateEVSRefsIfMissing(diaryNoteListVaultWithRefs, null);

            if (diaryNoteListVaultWithRefs != null) {
                for (int i = 0; i < diaryNoteListVaultBeforeGeneratingRefs.getIdentifiables().size(); i++) {
                    if (! KmehrMatcher.equal(diaryNoteListVaultBeforeGeneratingRefs.getIdentifiables().get(i), diaryNoteListVaultWithRefs.getIdentifiables().get(i))) {
                        KmehrWithReference diaryNote = diaryNoteListVaultWithRefs.getIdentifiables().get(i);
                        updateVitalinkURIForUpdate(diaryNote.getIdentifiableTransaction());
                        diaryNoteListVaultWithNewlyGeneratedRefs.getIdentifiables().add(diaryNote);
                    }
                }
            }

        } catch (IdenticalEVSRefsFoundException | MultipleEVSRefsInTransactionFoundException e) {
            throw new UploaderException(e);
        }

        diareNoteService.putTransactions(patient, diaryNoteListVaultWithNewlyGeneratedRefs);

    }

    @Override
    public void updateREF(Patient patient, KmehrWithReferenceList diaryNoteListToUpdate, String actorId) throws VitalinkException, UploaderException {

        diareNoteService.authenticate(readAuthenticationConfig(actorId));

        if (CollectionsUtil.emptyOrNull(diaryNoteListToUpdate.getList())) {
            LOG.info("No diarynotes provided for update");
            return;
        } else {
            LOG.info("About to update " + CollectionsUtil.size(diaryNoteListToUpdate.getList()) + " diarynotes in the vault for patientID: " + patient.getId());
        }

        try {
            refExtractor.extractEVSRefs(diaryNoteListToUpdate);
            refExtractor.validatePresenceEVSRefs(diaryNoteListToUpdate);
        } catch (Exception e) {
            throw new UploaderException(e);
        }

        KmehrWithReferenceList diaryNoteListInVault = diareNoteService.getKmehrWithReferenceList(patient);

        try {

            refExtractor.extractEVSRefs(diaryNoteListInVault);
            KmehrWithReferenceList diaryNoteList = KmehrMatcher.mergeForUpdate(diaryNoteListToUpdate, diaryNoteListInVault);
            for (KmehrWithReference diaryNote : diaryNoteList.getList()) {
                updateVitalinkURIForUpdate(diaryNote.getIdentifiableTransaction());
            }

            diareNoteService.putTransactions(patient, diaryNoteList);

        } catch (Exception e) {
            throw new UploaderException(e);
        }

    }

    private void updateVitalinkURIForUpdate(TransactionType transactionType) {

        List<IDKMEHR> idKmehrs = IDKmehrUtil.getIDKmehrs(transactionType.getIds(), IDKMEHRschemes.LOCAL);

        for (IDKMEHR idKmehr : idKmehrs) {
            if (LocalIdParser.isVitalinkLocalId(idKmehr)) {
                URI uri = URIBuilder.fromString(idKmehr.getValue());
                URI updateUri = URIConverter.convertToUpdate(uri);
                idKmehr.setValue(updateUri.format());
            }
        }

    }

    private AuthenticationConfig readAuthenticationConfig(String actorId) {
        AuthenticationConfig config = AuthenticationConfigReader.loadByName(actorId);
        return config;
    }

}
