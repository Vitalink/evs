package org.imec.ivlab.ehconnector.hub;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.Criteria;
import be.fgov.ehealth.hubservices.core.v3.GetLatestUpdateResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionSetResponse;
import be.fgov.ehealth.hubservices.core.v3.PatientIdType;
import be.fgov.ehealth.hubservices.core.v3.PutTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.PutTransactionSetResponse;
import be.fgov.ehealth.hubservices.core.v3.RevokeTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.SelectGetLatestUpdateType;
import be.fgov.ehealth.hubservices.core.v3.TransactionBaseType;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import javax.xml.bind.JAXBException;
import junit.framework.TestCase;
import lombok.extern.log4j.Log4j;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;
import org.imec.ivlab.core.config.EVSConfig;
import org.imec.ivlab.core.config.EVSProperties;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.kmehr.KmehrMarshaller;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.InvalidConfigurationException;
import org.imec.ivlab.ehconnector.hub.session.SessionManager;

@Log4j
public class HubServiceTest extends TestCase {

  public static final String actor = AuthenticationConfigReader.GP_EXAMPLE;
  public static final String SUMEHR_LOCAL_ID = "";
  public static final String MEDICATION_SCHEME_LOCAL_ID = "";
  public static final String INSZ = "";

  private HubService init(String actorName) throws InvalidConfigurationException, TechnicalConnectorException {
    EVSConfig.getInstance().setProperty(EVSProperties.CHOSEN_HUB, "VITALINK");
    EVSConfig.getInstance().setProperty(EVSProperties.SEARCH_TYPE, "GLOBAL");
    HubService hubService = new HubService();
    SessionManager.connectWith(AuthenticationConfigReader.loadByName(actorName));
    return hubService;
  }

  public void testGetLatestUpdate() throws VitalinkException, GatewaySpecificErrorException, TechnicalConnectorException, JAXBException {
    HubService hubService = init(actor);

    SelectGetLatestUpdateType selectGetLatestUpdateType = new SelectGetLatestUpdateType();
    Criteria criterium = new Criteria();
    criterium.setPatient(getPatientIdType());
    CDTRANSACTION cd = getCdtransaction();
    criterium.getCds().add(cd);
    selectGetLatestUpdateType.getCriterias().add(criterium);

    GetLatestUpdateResponse latestUpdateResponse = hubService.getLatestUpdate(selectGetLatestUpdateType);
    log.info(JAXBUtils.marshal(latestUpdateResponse));
  }

  public void testGetTransactionSet() throws VitalinkException, GatewaySpecificErrorException, JAXBException, TechnicalConnectorException {
    HubService hubService = init(actor);

    TransactionBaseType transactionBaseType = new TransactionBaseType();
    transactionBaseType.setId(getIdKmehr(MEDICATION_SCHEME_LOCAL_ID));
    GetTransactionSetResponse transactionSet = hubService.getTransactionSet(getPatientIdType(), transactionBaseType);
    log.info(JAXBUtils.marshal(transactionSet));
  }

  public void testPutTransactionSet() throws TransformationException, VitalinkException, GatewaySpecificErrorException, JAXBException, TechnicalConnectorException {
    HubService hubService = init(actor);

    Kmehrmessage kmehrmessage = loadKmehr("/kmehrs/test-putTransactionSet.xml");
    PutTransactionSetResponse putTransactionSetResponse = hubService.putTransactionSet(kmehrmessage);
    log.info(JAXBUtils.marshal(putTransactionSetResponse));
  }

  public void testGetTransaction() throws VitalinkException, TechnicalConnectorException, GatewaySpecificErrorException, JAXBException {
    HubService hubService = init(actor);

    TransactionBaseType transactionBaseType = new TransactionBaseType();
    transactionBaseType.setId(getIdKmehr(SUMEHR_LOCAL_ID));
    GetTransactionResponse transaction = hubService.getTransaction(getPatientIdType(), transactionBaseType);
    log.info(JAXBUtils.marshal(transaction));
  }

  public void testPutTransaction() throws VitalinkException, GatewaySpecificErrorException, TechnicalConnectorException, TransformationException, JAXBException {
    HubService hubService = init(actor);

    Kmehrmessage kmehrmessage = loadKmehr("/kmehrs/test-putTransaction.xml");
    PutTransactionResponse putTransactionResponse = hubService.putTransaction(kmehrmessage);
    log.info(JAXBUtils.marshal(putTransactionResponse));
  }

  public void testRevokeTransaction() throws VitalinkException, TechnicalConnectorException, GatewaySpecificErrorException, JAXBException {
    HubService hubService = init(actor);

    TransactionBaseType transactionBaseType = new TransactionBaseType();
    transactionBaseType.setId(getIdKmehr(SUMEHR_LOCAL_ID));
    RevokeTransactionResponse putTransactionResponse = hubService.revokeTransaction(getPatientIdType(), transactionBaseType);
    log.info(JAXBUtils.marshal(putTransactionResponse));
  }

  private IDKMEHR getIdKmehr(String value) {
    IDKMEHR idkmehr = new IDKMEHR();
    idkmehr.setS(IDKMEHRschemes.LOCAL);
    idkmehr.setSV("1.0");
    idkmehr.setSL("vitalinkuri");
    idkmehr.setValue(value);
    return idkmehr;
  }

  private Kmehrmessage loadKmehr(String location) throws TransformationException {
    String kmehrFileContent = IOUtils.getResourceAsString(location);
    return KmehrMarshaller.fromString(kmehrFileContent);
  }

  private PatientIdType getPatientIdType() {
    PatientIdType patientIdType = new PatientIdType();
    IDPATIENT idpatient = new IDPATIENT();
    idpatient.setS(IDPATIENTschemes.INSS);
    idpatient.setSV("1.0");
    idpatient.setValue(INSZ);
    patientIdType.getIds().add(idpatient);
    return patientIdType;
  }

  private CDTRANSACTION getCdtransaction() {
    CDTRANSACTION cd = new CDTRANSACTION();
    cd.setS(CDTRANSACTIONschemes.CD_TRANSACTION);
    cd.setSV("1.4");
    cd.setValue("medicationscheme");
    return cd;
  }
}