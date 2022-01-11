package org.imec.ivlab.ehconnector.business.diary;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionResponse;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.kmehr.KmehrConstants;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.kmehr.modifier.impl.KmehrStandardModifier;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.model.upload.KmehrWithReference;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.upload.extractor.DiaryNoteListExtractor;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.ehconnector.business.AbstractService;
import org.imec.ivlab.ehconnector.business.HubHelper;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.TransactionNotFoundException;
import org.imec.ivlab.ehconnector.hubflow.HubFlow;

public class DiaryNoteServiceImpl extends AbstractService implements DiaryNoteService {

    private final static Logger LOG = LogManager.getLogger(DiaryNoteServiceImpl.class);

    private static final TransactionType TRANSACTION_TYPE = TransactionType.DIARY_NOTE;
    private HubFlow hubFlow = new HubFlow();
    private HubHelper hubHelper = new HubHelper();

    public DiaryNoteServiceImpl() throws VitalinkException {
    }


    @Override
    public KmehrWithReferenceList getKmehrWithReferenceList(Patient patient) throws VitalinkException {
        try {
            List<GetTransactionResponse> individualTransactions = hubFlow.getIndividualTransactions(patient.getId(), TRANSACTION_TYPE);
            return new DiaryNoteListExtractor().getKmehrWithReferenceList(extractKmehrMessagesFromResponses(individualTransactions));
        } catch (TransactionNotFoundException e) {
            return new KmehrWithReferenceList();
        } catch (Exception e) {
            throw new VitalinkException(e);
        }
    }

    @Override
    public void putTransactions(Patient patient, KmehrWithReferenceList diaryNoteList) throws VitalinkException {

        if (CollectionsUtil.emptyOrNull(diaryNoteList.getList())) {
            return;
        }

        for (KmehrWithReference diaryNote : diaryNoteList.getList()) {
            try {
                hubFlow.putTransaction(diaryNoteToKmehr(patient, diaryNote));
            } catch (GatewaySpecificErrorException | TechnicalConnectorException e) {
                throw new VitalinkException(e);
            }
        }

    }

    @Override
    public void revokeTransactions(Patient patient, KmehrWithReferenceList diaryNoteList) throws VitalinkException {

        if (CollectionsUtil.emptyOrNull(diaryNoteList.getList())) {
            return;
        }

        for (KmehrWithReference diaryNote : diaryNoteList.getList()) {
            try {
                hubFlow.revokeTransaction(patient.getId(), diaryNoteToKmehr(patient, diaryNote));
            } catch (GatewaySpecificErrorException | TechnicalConnectorException e) {
                throw new VitalinkException(e);
            }
        }

    }

    private List<Kmehrmessage> extractKmehrMessagesFromResponses(List<GetTransactionResponse> getTransactionResponses) {

        List<Kmehrmessage> kmehrmessages = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(getTransactionResponses)) {
            return kmehrmessages;
        }

        for (GetTransactionResponse getTransactionResponse : getTransactionResponses) {
            if (getTransactionResponse != null) {
                kmehrmessages.add(getTransactionResponse.getKmehrmessage());
            }
        }

        return kmehrmessages;

    }


    private Kmehrmessage diaryNoteToKmehr(Patient patient, KmehrWithReference diaryNote) throws VitalinkException {
        if (diaryNote == null) {
            throw new RuntimeException("No diarynote provided!");
        }
        FolderType folderType = KmehrMessageUtil.getFolderType(diaryNote.getKmehrMessage());
        Kmehrmessage template = getKmehrmessageTemplate(patient, folderType);
        return template;

    }

    private Kmehrmessage getKmehrmessageTemplate(Patient patient, FolderType folderType) throws VitalinkException {
        try {
            Kmehrmessage transactionMessage = hubHelper.createTransactionMessage(patient, folderType);
            KmehrStandardModifier kmehrStandardModifier = new KmehrStandardModifier(KmehrConstants.KMEHR_STANDARD_GATEWAY_DIARYNOTE);
            kmehrStandardModifier.modify(transactionMessage);
            return transactionMessage;
        } catch (Exception e) {
            throw new VitalinkException(e);
        }
    }


}
