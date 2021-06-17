package org.imec.ivlab.datagenerator.uploader.service;

import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import java.util.List;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;
import org.imec.ivlab.core.authentication.model.AuthenticationConfig;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.kmehr.KmehrHelper;
import org.imec.ivlab.core.kmehr.model.localid.LocalIdParser;
import org.imec.ivlab.core.kmehr.model.localid.URI;
import org.imec.ivlab.core.kmehr.model.localid.util.URIBuilder;
import org.imec.ivlab.core.kmehr.model.localid.util.URIConverter;
import org.imec.ivlab.core.kmehr.model.util.IDKmehrUtil;
import org.imec.ivlab.core.model.evsref.extractor.impl.SumehrEVSRefExtractor;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.model.upload.KmehrWithReference;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;
import org.imec.ivlab.core.model.upload.msentrylist.exception.IdenticalEVSRefsFoundException;
import org.imec.ivlab.core.model.upload.msentrylist.exception.MultipleEVSRefsInTransactionFoundException;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.datagenerator.uploader.exception.UploaderException;
import org.imec.ivlab.ehconnector.business.HubHelper;
import org.imec.ivlab.ehconnector.business.sumehr.SumehrService;
import org.imec.ivlab.ehconnector.business.sumehr.SumehrServiceImpl;

public class SumehrUploaderImpl implements SumehrUploader, Uploader {

    private final static Logger LOG = Logger.getLogger(SumehrUploaderImpl.class);

    private SumehrService sumehrService;
    private HubHelper hubHelper = new HubHelper();
    private SumehrEVSRefExtractor refExtractor;

    public SumehrUploaderImpl() throws VitalinkException {
        this.sumehrService = new SumehrServiceImpl();
        this.refExtractor = new SumehrEVSRefExtractor();
    }

    @Override
    public void add(Patient patient, KmehrWithReferenceList sumehrListToAdd, String actorId) throws UploaderException, VitalinkException {

        try {
            refExtractor.extractEVSRefs(sumehrListToAdd);
        } catch (MultipleEVSRefsInTransactionFoundException e) {
            throw new UploaderException(e);
        }

        if (CollectionsUtil.emptyOrNull(sumehrListToAdd.getList())) {
            LOG.info("No sumehrs provided for upload");
            return;
        } else {
            LOG.info("About to add " + CollectionsUtil.size(sumehrListToAdd.getList()) + " sumehrs to the vault for patientID: " + patient.getId());
        }

        sumehrService.authenticate(readAuthenticationConfig(actorId));

        KmehrWithReferenceList sumehrListInVault = sumehrService.getKmehrWithReferenceList(patient);

        try {
            refExtractor.extractEVSRefs(sumehrListInVault);
            refExtractor.validateEVSRefUniqueness(refExtractor.merge(sumehrListToAdd, sumehrListInVault));

            refExtractor.generateEVSRefsIfMissing(sumehrListToAdd, sumehrListInVault);

            KmehrHelper.removeLocalIds(sumehrListToAdd);
            sumehrService.putTransactions(patient, sumehrListToAdd);

        } catch (Exception e) {
            throw new VitalinkException(e);
        }

    }

    @Override
    public void empty(Patient patient, String actorId) throws VitalinkException {

        sumehrService.authenticate(readAuthenticationConfig(actorId));

        KmehrWithReferenceList sumehrListInVault = sumehrService.getKmehrWithReferenceListOfCurrentActor(patient);

        if (CollectionsUtil.emptyOrNull(sumehrListInVault.getList())) {
            LOG.info("No sumehrs of the current actor in vault");
            return;
        } else {
            LOG.info("About to remove " + CollectionsUtil.size(sumehrListInVault.getList()) + " sumehrs from the vault for patientID: " + patient.getId() + " and actor: " + actorId);
        }

        sumehrService.revokeTransactions(patient, sumehrListInVault);

    }

    @Override
    public void replace(Patient patient, KmehrWithReferenceList sumehrList, String actorId) throws VitalinkException, UploaderException {

        empty(patient, actorId);

        add(patient, sumehrList, actorId);

    }

    @Override
    public void generateREF(Patient patient, String actorId) throws VitalinkException, UploaderException {

        sumehrService.authenticate(readAuthenticationConfig(actorId));

        KmehrWithReferenceList sumehrListVaultBeforeGeneratingRefs = sumehrService.getKmehrWithReferenceList(patient);
        KmehrWithReferenceList sumehrListVaultWithRefs = SerializationUtils.clone(sumehrListVaultBeforeGeneratingRefs);
        KmehrWithReferenceList sumehrListVaultWithNewlyGeneratedRefs = SerializationUtils.clone(sumehrListVaultWithRefs);
        sumehrListVaultWithNewlyGeneratedRefs.getIdentifiables().clear();

        if (CollectionsUtil.emptyOrNull(sumehrListVaultBeforeGeneratingRefs.getList())) {
            LOG.info("No sumehrs in vault; No need to generate REFs.");
            return;
        } else {
            LOG.info("About to generate REFs for " + CollectionsUtil.size(sumehrListVaultBeforeGeneratingRefs.getList()) + " sumehrs in the vault for patientID: " + patient.getId());
        }

        try {
            refExtractor.extractEVSRefs(sumehrListVaultWithRefs);
            refExtractor.validateEVSRefUniqueness(sumehrListVaultWithRefs);
            refExtractor.generateEVSRefsIfMissing(sumehrListVaultWithRefs, null);

            if (sumehrListVaultWithRefs != null) {
                for (int i = 0; i < sumehrListVaultBeforeGeneratingRefs.getIdentifiables().size(); i++) {
                    if (! KmehrMatcher.equal(sumehrListVaultBeforeGeneratingRefs.getIdentifiables().get(i), sumehrListVaultWithRefs.getIdentifiables().get(i))) {
                        KmehrWithReference sumehr = sumehrListVaultWithRefs.getIdentifiables().get(i);
                        updateVitalinkURIForUpdate(sumehr.getIdentifiableTransaction());
                        sumehrListVaultWithNewlyGeneratedRefs.getIdentifiables().add(sumehr);
                    }
                }
            }

        } catch (IdenticalEVSRefsFoundException | MultipleEVSRefsInTransactionFoundException e) {
            throw new UploaderException(e);
        }

        sumehrService.putTransactions(patient, sumehrListVaultWithNewlyGeneratedRefs);

    }

    @Override
    public void removeREF(Patient patient, KmehrWithReferenceList sumehrListToRemove, String actorId) throws VitalinkException, UploaderException {

        sumehrService.authenticate(readAuthenticationConfig(actorId));

        if (CollectionsUtil.emptyOrNull(sumehrListToRemove.getList())) {
            LOG.info("No sumehrs provided for removal");
            return;
        } else {
            LOG.info("About to remove " + CollectionsUtil.size(sumehrListToRemove.getList()) + " sumehrs from the vault for patientID: " + patient.getId());
        }

        try {
            refExtractor.extractEVSRefs(sumehrListToRemove);
            refExtractor.validatePresenceEVSRefs(sumehrListToRemove);
        } catch (Exception e) {
            throw new UploaderException(e);
        }

        KmehrWithReferenceList sumehrListInVault = sumehrService.getKmehrWithReferenceList(patient);

        try {
            refExtractor.extractEVSRefs(sumehrListInVault);
            KmehrWithReferenceList sumehrList = KmehrMatcher.ensureEVSRefsMatchWithVault(sumehrListToRemove, sumehrListInVault);
            sumehrService.revokeTransactions(patient, sumehrList);
        } catch (Exception e) {
            throw new UploaderException(e);
        }


    }

    @Override
    public void updateREF(Patient patient, KmehrWithReferenceList sumehrListToUpdate, String actorId) throws VitalinkException, UploaderException {

        sumehrService.authenticate(readAuthenticationConfig(actorId));

        if (CollectionsUtil.emptyOrNull(sumehrListToUpdate.getList())) {
            LOG.info("No sumehrs provided for update");
            return;
        } else {
            LOG.info("About to update " + CollectionsUtil.size(sumehrListToUpdate.getList()) + " sumehrs in the vault for patientID: " + patient.getId());
        }

        try {
            refExtractor.extractEVSRefs(sumehrListToUpdate);
            refExtractor.validatePresenceEVSRefs(sumehrListToUpdate);
        } catch (Exception e) {
            throw new UploaderException(e);
        }

        KmehrWithReferenceList sumehrListInVault = sumehrService.getKmehrWithReferenceList(patient);

        try {

            refExtractor.extractEVSRefs(sumehrListInVault);
            KmehrWithReferenceList sumehrList = KmehrMatcher.mergeForUpdate(sumehrListToUpdate, sumehrListInVault);
            for (KmehrWithReference sumehr : sumehrList.getList()) {
                updateVitalinkURIForUpdate(sumehr.getIdentifiableTransaction());
            }

            sumehrService.putTransactions(patient, sumehrList);

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
