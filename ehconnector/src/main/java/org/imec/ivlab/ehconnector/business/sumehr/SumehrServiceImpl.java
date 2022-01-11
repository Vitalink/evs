package org.imec.ivlab.ehconnector.business.sumehr;

import static org.imec.ivlab.ehconnector.hub.util.AuthorUtil.createAuthorPersonHcParties;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionResponse;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.kmehr.model.util.HCPartyUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.model.upload.KmehrWithReference;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.upload.extractor.SumehrListExtractor;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.ehconnector.business.AbstractService;
import org.imec.ivlab.ehconnector.business.HubHelper;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.TransactionNotFoundException;
import org.imec.ivlab.ehconnector.hubflow.HubFlow;

public class SumehrServiceImpl extends AbstractService implements SumehrService {

    private final static Logger LOG = LogManager.getLogger(SumehrServiceImpl.class);

    private static final TransactionType TRANSACTION_TYPE = TransactionType.SUMEHR;
    private HubFlow hubFlow = new HubFlow();
    private HubHelper hubHelper = new HubHelper();

    public SumehrServiceImpl() {
    }


    @Override
    public KmehrWithReferenceList getKmehrWithReferenceList(Patient patient) throws VitalinkException {
        try {
            List<GetTransactionResponse> individualTransactions = hubFlow.getIndividualTransactions(patient.getId(), TRANSACTION_TYPE);
            return new SumehrListExtractor().getKmehrWithReferenceList(extractKmehrMessagesFromResponses(individualTransactions));
        } catch (TransactionNotFoundException e) {
            return new KmehrWithReferenceList();
        } catch (Exception e) {
            throw new VitalinkException(e);
        }
    }

    @Override
    public KmehrWithReferenceList getKmehrWithReferenceListOfCurrentActor(Patient patient) throws VitalinkException {
        List<HcpartyType> authorsOfAction = null;
        try {
            authorsOfAction = createAuthorPersonHcParties();
        } catch (TechnicalConnectorException e) {
            throw new RuntimeException(e);
        }
        return filterByAuthor(getKmehrWithReferenceList(patient), authorsOfAction);
    }

    private KmehrWithReferenceList filterByAuthor(KmehrWithReferenceList sumehrList, List<HcpartyType> authorsOfAction) {
        sumehrList.setList(sumehrList.getList().stream().filter(sumehr -> actionAuthorMatchesAuthorOfSumehr(sumehr, authorsOfAction)).collect(Collectors.toList()));
        return sumehrList;
    }

    private boolean actionAuthorMatchesAuthorOfSumehr(KmehrWithReference sumehr, List<HcpartyType> authorsOfAction) {
        AuthorType sumehrAuthor = Optional
            .ofNullable(sumehr.getIdentifiableTransaction())
            .map(be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType::getAuthor)
            .orElse(null);

        if (sumehrAuthor == null || sumehrAuthor.getHcparties() == null || authorsOfAction == null ) {
            return false;
        }

        return Stream.concat(
            authorsOfAction.stream().flatMap(author -> HCPartyUtil.getIDHcParties(author, IDHCPARTYschemes.INSS).stream()),
            authorsOfAction.stream().flatMap(author -> HCPartyUtil.getIDHcParties(author, IDHCPARTYschemes.ID_HCPARTY).stream())
        ).allMatch(idhcpartyAction -> containsAnIdenticalId(sumehrAuthor.getHcparties(), idhcpartyAction));

    }

    private boolean containsAnIdenticalId(List<HcpartyType> hcparties, IDHCPARTY idhcpartyAction) {
        return hcparties
                    .stream()
                    .flatMap(hcpartyType -> hcpartyType.getIds().stream())
                    .anyMatch(
                        idhcpartySumehr ->
                            idhcpartySumehr.getS() != null &&
                            idhcpartySumehr.getS().equals(idhcpartyAction.getS()) &&
                            idhcpartySumehr.getValue() != null &&
                            idhcpartySumehr.getValue().equals(idhcpartyAction.getValue()));
    }

    @Override
    public void putTransactions(Patient patient, KmehrWithReferenceList sumehrList) throws VitalinkException {

        if (CollectionsUtil.emptyOrNull(sumehrList.getList())) {
            return;
        }

        for (KmehrWithReference sumehr : sumehrList.getList()) {
            try {
                hubFlow.putTransaction(sumehrToKmehr(patient, sumehr));
            } catch (GatewaySpecificErrorException | TechnicalConnectorException e) {
                throw new VitalinkException(e);
            }
        }

    }

    @Override
    public void revokeTransactions(Patient patient, KmehrWithReferenceList sumehrList) throws VitalinkException {

        if (CollectionsUtil.emptyOrNull(sumehrList.getList())) {
            return;
        }

        for (KmehrWithReference sumehr : sumehrList.getList()) {
            try {
                hubFlow.revokeTransaction(patient.getId(), sumehrToKmehr(patient, sumehr));
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
            if(getTransactionResponse != null){
                kmehrmessages.add(getTransactionResponse.getKmehrmessage());
            }
        }

        return kmehrmessages;

    }


    private Kmehrmessage sumehrToKmehr(Patient patient, KmehrWithReference sumehr) throws VitalinkException {
        if (sumehr == null) {
            throw new RuntimeException("No sumehr provided!");
        }
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
