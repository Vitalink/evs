package org.imec.ivlab.ehconnector.business.sumehr;

import be.fgov.ehealth.hubservices.core.v3.GetTransactionResponse;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.model.upload.sumehrlist.Sumehr;
import org.imec.ivlab.core.model.upload.sumehrlist.SumehrList;
import org.imec.ivlab.core.model.upload.sumehrlist.SumehrListExtractor;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.ehconnector.business.AbstractService;
import org.imec.ivlab.ehconnector.business.HubHelper;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.TransactionNotFoundException;
import org.imec.ivlab.ehconnector.hubflow.HubFlow;

import java.util.ArrayList;
import java.util.List;

public class SumehrServiceImpl extends AbstractService implements SumehrService {

    private final static Logger LOG = Logger.getLogger(SumehrServiceImpl.class);

    private static final TransactionType TRANSACTION_TYPE = TransactionType.SUMEHR;
    private HubFlow hubFlow = new HubFlow();
    private HubHelper hubHelper = new HubHelper();

    public SumehrServiceImpl() throws VitalinkException {
    }


    @Override
    public SumehrList getSumehrList(Patient patient) throws VitalinkException {
        try {
            List<GetTransactionResponse> individualTransactions = hubFlow.getIndividualTransactions(patient.getId(), TRANSACTION_TYPE);
            return SumehrListExtractor.getSumehrList(extractKmehrMessagesFromResponses(individualTransactions));
        } catch (TransactionNotFoundException e) {
            return new SumehrList();
        } catch (Exception e) {
            throw new VitalinkException(e);
        }
    }

    @Override
    public void putTransactions(Patient patient, SumehrList sumehrList) throws VitalinkException {

        if (CollectionsUtil.emptyOrNull(sumehrList.getList())) {
            return;
        }

        for (Sumehr sumehr : sumehrList.getList()) {
            try {
                hubFlow.putTransaction(sumehrToKmehr(patient, sumehr));
            } catch (GatewaySpecificErrorException e) {
                throw new VitalinkException(e);
            }
        }

    }

    @Override
    public void revokeTransactions(Patient patient, SumehrList sumehrList) throws VitalinkException {

        if (CollectionsUtil.emptyOrNull(sumehrList.getList())) {
            return;
        }

        for (Sumehr sumehr : sumehrList.getList()) {
            try {
                hubFlow.revokeTransaction(patient.getId(), sumehrToKmehr(patient, sumehr));
            } catch (GatewaySpecificErrorException e) {
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
            if(getTransactionResponse != null){
                kmehrmessages.add(getTransactionResponse.getKmehrmessage());
            }
        }

        return kmehrmessages;

    }


    private Kmehrmessage sumehrToKmehr(Patient patient, Sumehr sumehr) throws VitalinkException {
        FolderType folderType = KmehrMessageUtil.getFolderType(sumehr.getKmehrMessage());
        Kmehrmessage template = getKmehrmessageTemplate(patient, folderType);
        return template;

    }

    private Kmehrmessage getKmehrmessageTemplate(Patient patient, FolderType folderType) throws VitalinkException {
        try {
            return hubHelper.createTransactionMessage(patient, folderType);
        } catch (Exception e) {
            throw new VitalinkException(e);
        }
    }


}
