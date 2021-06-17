/*
 * Copyright (c) eHealth
 */
package org.imec.ivlab.ehconnector.business;

import be.ehealth.business.common.domain.Patient;
import be.ehealth.businessconnector.hubv3.service.ServiceFactory;
import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.config.Configuration;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.idgenerator.IdGeneratorFactory;
import be.ehealth.technicalconnector.utils.MarshallerHelper;
import be.ehealth.technicalconnector.utils.SessionUtil;
import be.ehealth.technicalconnector.utils.TemplateEngineUtils;
import be.fgov.ehealth.hubservices.core.v3.Consent;
import be.fgov.ehealth.hubservices.core.v3.ConsentType;
import be.fgov.ehealth.hubservices.core.v3.Criteria;
import be.fgov.ehealth.hubservices.core.v3.HCPartyAdaptedType;
import be.fgov.ehealth.hubservices.core.v3.HCPartyIdType;
import be.fgov.ehealth.hubservices.core.v3.LocalSearchType;
import be.fgov.ehealth.hubservices.core.v3.PatientIdType;
import be.fgov.ehealth.hubservices.core.v3.SelectGetHCPartyPatientConsentType;
import be.fgov.ehealth.hubservices.core.v3.SelectGetLatestUpdateType;
import be.fgov.ehealth.hubservices.core.v3.SelectGetPatientAuditTrailType;
import be.fgov.ehealth.hubservices.core.v3.SelectGetPatientConsentType;
import be.fgov.ehealth.hubservices.core.v3.TherapeuticLinkType;
import be.fgov.ehealth.hubservices.core.v3.TransactionBaseType;
import be.fgov.ehealth.hubservices.core.v3.TransactionIdType;
import be.fgov.ehealth.hubservices.core.v3.TransactionWithPeriodType;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDADDRESS;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDADDRESSschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONSENTvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCOUNTRY;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCOUNTRYschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDSEX;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDSEXvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDSTANDARD;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTELECOM;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTELECOMschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTHERAPEUTICLINK;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTHERAPEUTICLINKschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.AddressType;
import be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType;
import be.fgov.ehealth.standards.kmehr.schema.v1.CountryType;
import be.fgov.ehealth.standards.kmehr.schema.v1.DateType;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HeaderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.PersonType;
import be.fgov.ehealth.standards.kmehr.schema.v1.RecipientType;
import be.fgov.ehealth.standards.kmehr.schema.v1.SenderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.SexType;
import be.fgov.ehealth.standards.kmehr.schema.v1.StandardType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TelecomType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.kmehr.KmehrMarshaller;
import org.imec.ivlab.core.kmehr.modifier.impl.PatientDataModifier;
import org.imec.ivlab.core.util.DateUtils;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.ehconnector.business.medicationscheme.hubhelper.HubConfigCommon;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author EHP
 */
public class HubHelper {

    private static final Configuration config = ConfigFactory.getConfigValidator();

    private static final Logger LOG = LoggerFactory.getLogger(HubHelper.class);

    private static final String TEMPLATE_FILE_PUT_TRANSACTION_SET = "PutTransactionSet - request - template.xml";
    private static final String TEMPLATE_FILE_PUT_TRANSACTION = "PutTransaction - request - template.xml";

    private String testFilesLocation;
    private Patient testPatient;
    
    public HubHelper() {
        this.testPatient = null;
        this.testFilesLocation = "vitalink";
    }

    public String expectedResponse(String scenarioName) {
        Map<String, Object> velocityContext = new HashMap<String, Object>();
        velocityContext.put("requestDate", DateTimeFormat.forPattern("dd/MM/yyyy").print(new DateTime()));
        return TemplateEngineUtils.generate(velocityContext, "/expected/" + testFilesLocation + "/" + scenarioName + " - response.xml");
    }


    /**
     * Create an IDPATIENT for Niss
     */
    private IDPATIENT createIdPatientInss() {
        IDPATIENT idPatient = new IDPATIENT();
        idPatient.setS(IDPATIENTschemes.INSS);
        idPatient.setSV("1.0");
        idPatient.setValue(testPatient.getInss());
        return idPatient;
    }

    private IDPATIENT createIdPatientInss(String patientId) {
        IDPATIENT idPatient = new IDPATIENT();
        idPatient.setS(IDPATIENTschemes.INSS);
        idPatient.setSV("1.0");
        idPatient.setValue(patientId);
        return idPatient;
    }

    /**
     * Create an IDPATIENT for Niss
     */
    private IDPATIENT createIdPatientCardno(String value) {
        IDPATIENT idPatient = new IDPATIENT();
        idPatient.setS(IDPATIENTschemes.EID_CARDNO);
        idPatient.setSV("1.0");
        idPatient.setValue(value);
        return idPatient;
    }

    /**
     * Create an IDHCPARTY for Niss
     */
    private IDHCPARTY createIdHcPartyNiss(String value) {
        IDHCPARTY idHcparty = new IDHCPARTY();
        idHcparty.setS(IDHCPARTYschemes.INSS);
        idHcparty.setSV("1.0");
        idHcparty.setValue(value);
        return idHcparty;
    }

    /**
     * Create an IDHCPARTY for Nihii
     */
    public IDHCPARTY createIdHcPartyNihii() throws TechnicalConnectorException {
        IDHCPARTY idHcparty = new IDHCPARTY();
        idHcparty.setS(IDHCPARTYschemes.ID_HCPARTY);
        idHcparty.setSV("1.0");
        idHcparty.setValue(SessionUtil.getNihii11());
        return idHcparty;
    }

    /**
     * Create an IDKMEHR with the specified value
     */
    public IDKMEHR createMessageId(String value) {
        IDKMEHR id = new IDKMEHR();
        id.setS(IDKMEHRschemes.ID_KMEHR);
        id.setSV("1.0");
        id.setValue(value);
        return id;
    }

    /**
     * Create a HcPartyId with the informations of the professional
     */
    public HCPartyIdType createHcPartyIdProfessional() throws TechnicalConnectorException {
        HCPartyIdType hcParty = new HCPartyIdType();
        IDHCPARTY idHcparty = createIdHcPartyNihii();
        hcParty.getIds().add(idHcparty);
        idHcparty = createIdHcPartyNihii();
        hcParty.getIds().add(idHcparty);
        return hcParty;
    }

    /**
     * Create the professional HcParty
     */
    private HcpartyType createHcPartyProfessional() {
        HcpartyType hcParty = new HcpartyType();
        hcParty.setFamilyname(HubConfigCommon.PROF_LASTNAME);
        hcParty.setFirstname(HubConfigCommon.PROF_FIRSTNAME);
        CDHCPARTY cdHcParty = createCdHcPartyProfession();
        hcParty.getCds().add(cdHcParty);
        IDHCPARTY idHcparty = createIdHcPartyNiss(HubConfigCommon.PROF_NISS);
        hcParty.getIds().add(idHcparty);
        return hcParty;
    }

    /**
     * Create a PatientIdType Used by createConsentType
     */
    public PatientIdType createPatientIdType() {
        PatientIdType patient = new PatientIdType();
        IDPATIENT idPatient = createIdPatientInss();
        patient.getIds().add(idPatient);
        if (this.testPatient.getEidCardNumber() != null) {
            IDPATIENT idPatientCardNo = createIdPatientCardno(testPatient.getEidCardNumber());
            patient.getIds().add(idPatientCardNo);
        }
        try {
            LOG.info("just created patient: " + JAXBUtils.marshal(patient, "patient"));
        } catch (JAXBException e) {
            LOG.info("Error logging patient details", e);
        }
        return patient;
    }

    public PatientIdType createPatientIdType(String createIdPatientInss) {
        return createPatientIdType(createIdPatientInss, null);
    }

    public PatientIdType createPatientIdType(String createIdPatientInss, String eidno) {
        PatientIdType patient = new PatientIdType();
        IDPATIENT idPatient = createIdPatientInss(createIdPatientInss);
        patient.getIds().add(idPatient);

        if (eidno != null) {
            IDPATIENT idEidNo = createIdPatientCardno(eidno);
            patient.getIds().add(idEidNo);
        }

        return patient;
    }

    /**
     * Create a cd with the profession in it Used by createHcPartyProfessional
     */
    public CDHCPARTY createCdHcPartyProfession() {
        CDHCPARTY cdHcParty = new CDHCPARTY();
        cdHcParty.setS(CDHCPARTYschemes.CD_HCPARTY);
        cdHcParty.setSV("1.0");
        cdHcParty.setValue(HubConfigCommon.PROF_PROFESSION);
        return cdHcParty;
    }

    /**
     * Create the header of the transaction
     */
    public HeaderType createHeader(String standardDate) throws Exception {
        HeaderType header = new HeaderType();

        StandardType standard = new StandardType();
        CDSTANDARD cd = new CDSTANDARD();
        cd.setSV("1.4");
        cd.setValue(standardDate);
        cd.setS("CD-STANDARD");
        standard.setCd(cd);

        header.setStandard(standard);
        header.getIds().add(createMessageId(SessionUtil.getNihii11() + "." + IdGeneratorFactory.getIdGenerator().generateId()));
        LocalDate now = LocalDate.now();
        header.setDate(DateUtils.toXmlGregorianCalendar(now));
        header.setTime(DateUtils.toXmlGregorianCalendar(now));
        header.getRecipients().add(createHubRecipient());
        header.setSender(createSender());
        return header;
    }

    /**
     * Create the author of the operation
     */
    private SenderType createSender() throws TechnicalConnectorException {
        HcpartyType hcParty = new HcpartyType();

        IDHCPARTY doctorNISS = new IDHCPARTY();
        IDHCPARTY doctorNIHII = new IDHCPARTY();
        CDHCPARTY hcPartytype = new CDHCPARTY();

        doctorNIHII.setS(IDHCPARTYschemes.ID_HCPARTY);
        doctorNIHII.setSV("1.0");
        doctorNIHII.setValue(SessionUtil.getNihii());

        doctorNISS.setS(IDHCPARTYschemes.INSS);
        doctorNISS.setSV("1.0");
        doctorNISS.setValue(HubConfigCommon.PROF_NISS);

        hcPartytype.setS(CDHCPARTYschemes.CD_HCPARTY);
        hcPartytype.setSV("1.0");
        hcPartytype.setValue(HubConfigCommon.PROF_PROFESSION);

        hcParty.getIds().add(doctorNIHII);
        hcParty.getIds().add(doctorNISS);
        hcParty.getCds().add(hcPartytype);

        hcParty.setFamilyname(HubConfigCommon.PROF_LASTNAME);
        hcParty.setFirstname(HubConfigCommon.PROF_FIRSTNAME);

        SenderType sender = new SenderType();
        sender.getHcparties().add(hcParty);

        return sender;
    }

    /**
     * Create the author of the operation
     */
    public AuthorType createAuthor() {
        AuthorType author = new AuthorType();
        HcpartyType hcParty = createHcPartyProfessional();
        author.getHcparties().add(hcParty);
        return author;
    }

    /**
     * Create the Hub of the operation
     */
    private RecipientType createHubRecipient() {
        HcpartyType hub = new HcpartyType();

        IDHCPARTY id = new IDHCPARTY();
        id.setValue(config.getProperty(ServiceFactory.PROP_HUBID));
        id.setS(IDHCPARTYschemes.ID_HCPARTY);
        id.setSV("1.0");
        hub.getIds().add(id);

        CDHCPARTY cd = new CDHCPARTY();
        cd.setValue("hub");
        cd.setSV("1.0");
        cd.setS(CDHCPARTYschemes.CD_HCPARTY);
        hub.getCds().add(cd);

        hub.setName(HubConfigCommon.HUB_NAME);
        RecipientType recipient = new RecipientType();
        recipient.getHcparties().add(hub);
        return recipient;
    }

    /**
     * Create a consentType used for consent operations
     */
    public ConsentType createConsentType() {
        ConsentType consent = new ConsentType();

        CDCONSENT cdConsent = new CDCONSENT();
        cdConsent.setS(CDCONSENTschemes.CD_CONSENTTYPE);
        cdConsent.setSV("1.0");
        cdConsent.setValue(CDCONSENTvalues.RETROSPECTIVE);
        consent.getCds().add(cdConsent);

        consent.setAuthor(createAuthor());
        consent.setPatient(createPatientIdType());
        consent.setSigndate(new DateTime());

        return consent;
    }

    /**
     * Create a TherapeuticLinkType used for ther link operations
     */
    public TherapeuticLinkType createTherapeuticLinkType() throws TechnicalConnectorException {
        TherapeuticLinkType therapeuticLink = new TherapeuticLinkType();

        CDTHERAPEUTICLINK cdTherLink = new CDTHERAPEUTICLINK();
        cdTherLink.setS(CDTHERAPEUTICLINKschemes.CD_THERAPEUTICLINKTYPE);
        cdTherLink.setSV("1.0");
        cdTherLink.setValue("gpconsultation");
        therapeuticLink.setCd(cdTherLink);

        therapeuticLink.setHcparty(createHcPartyIdProfessional());
        therapeuticLink.setPatient(createPatientIdType());
        therapeuticLink.setStartdate(new DateTime());
        return therapeuticLink;
    }

    /**
     * Create the transaction id with the specified value
     */
    public IDKMEHR createIdKmehr(String sl, String sv, String value,IDKMEHRschemes s) {
        IDKMEHR id = new IDKMEHR();
        id.setS(s);
        id.setSL(sl);
        id.setSV(sv);
        id.setValue(value);
        return id;
    }

    public IDKMEHR createIDKmehr(String value) {
        IDKMEHR idKmehr = new IDKMEHR();
        idKmehr.setSV("1.0");
        idKmehr.setS(IDKMEHRschemes.ID_KMEHR);
        idKmehr.setValue(value);
        return idKmehr;
    }

    public IDKMEHR createIDKmehrVitalinkURI(String value) {
        IDKMEHR idKmehr = new IDKMEHR();
        idKmehr.setSV("1.0");
        idKmehr.setS(IDKMEHRschemes.LOCAL);
        idKmehr.setSL("vitalinkuri");
        idKmehr.setValue(value);
        return idKmehr;
    }

    /**
     * Create the testPatient needed for some transaction
     */
    public PersonType createPatientForTransaction() {
        PersonType patient = new PersonType();

        IDPATIENT kmehrPatientId = new IDPATIENT();
        kmehrPatientId.setS(IDPATIENTschemes.INSS);
        kmehrPatientId.setSV("1.0");
        kmehrPatientId.setValue(testPatient.getInss());
        patient.getIds().add(kmehrPatientId);

        patient.getFirstnames().add(testPatient.getFirstName());
        patient.setFamilyname(testPatient.getLastName());

        SexType sexType = new SexType();
        CDSEX cdSex = new CDSEX();
        cdSex.setValue(CDSEXvalues.MALE);
        cdSex.setS("CD-SEX");
        cdSex.setSV("1.0");
        sexType.setCd(cdSex);
        patient.setSex(sexType);
        return patient;
    }

    /**
     * Create the testPatient needed for the putPatient operation
     */
    public PersonType createPatient() {
        PersonType person = new PersonType();
        person.getFirstnames().add(testPatient.getFirstName());
        person.setFamilyname(testPatient.getLastName());
        person.setRecorddatetime(DateUtils.getCalendar());
        person.setUsuallanguage("fr");

        DateType dateType = new DateType();
        LocalDate localDate = LocalDate.of(1991, 12, 12);
        try {
            dateType.setDate(DateUtils.toXmlGregorianCalendar(localDate));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        person.setBirthdate(dateType);

        PersonType.Nationality nationality = new PersonType.Nationality();
        CDCOUNTRY cdCountry = new CDCOUNTRY();
        cdCountry.setS(CDCOUNTRYschemes.CD_FED_COUNTRY);
        cdCountry.setSV("1.0");
        cdCountry.setValue("BE");
        nationality.setCd(cdCountry);
        person.setNationality(nationality);

        SexType sex = new SexType();
        CDSEX cdSex = new CDSEX();
        cdSex.setS("CD-SEX");
        cdSex.setSV("1.0");
        cdSex.setValue(CDSEXvalues.MALE);
        sex.setCd(cdSex);
        person.setSex(sex);

        person.getIds().add(createIdPatientInss());
        return person;
    }

    public HCPartyAdaptedType createHcPartyAdaptedType() throws TechnicalConnectorException {
        HCPartyAdaptedType hcParty = new HCPartyAdaptedType();
        hcParty.setFamilyname(HubConfigCommon.PROF_LASTNAME);
        hcParty.setFirstname(HubConfigCommon.PROF_FIRSTNAME);
        CDHCPARTY cdHcParty = createCdHcPartyProfession();
        hcParty.getCds().add(cdHcParty);
        IDHCPARTY idHcpartyNihii = createIdHcPartyNihii();
        hcParty.getIds().add(idHcpartyNihii);
        hcParty.setRecorddatetime(new org.joda.time.DateTime());
        hcParty.getAddresses().add(createAddress());
        hcParty.getTelecoms().add(createTelecom());
        IDHCPARTY idHcpartyNiss = createIdHcPartyNiss(HubConfigCommon.PROF_NISS);
        hcParty.getIds().add(idHcpartyNiss);
        return hcParty;
    }

    /**
     * Create a selectGetPatientConsentType used for consent operations
     */
    public SelectGetPatientConsentType createSelectGetPatientConsentType() {
        SelectGetPatientConsentType patientConsent = new SelectGetPatientConsentType();

        Consent selectConsent = new Consent();
        CDCONSENT cdConsent = new CDCONSENT();
        cdConsent.setS(CDCONSENTschemes.CD_CONSENTTYPE);
        cdConsent.setSV("1.0");
        cdConsent.setValue(CDCONSENTvalues.RETROSPECTIVE);
        selectConsent.getCds().add(cdConsent);
        patientConsent.setConsent(selectConsent);

        patientConsent.setPatient(createPatientIdType());
        return patientConsent;
    }

    /**
     * Create a SelectGetHCPartyPatientConsentType used for ther link operations
     */
    public SelectGetHCPartyPatientConsentType createSelectGetHCPartyPatientConsentType() throws TechnicalConnectorException {
        SelectGetHCPartyPatientConsentType selectConsent = new SelectGetHCPartyPatientConsentType();

        CDTHERAPEUTICLINK cdTherLink = new CDTHERAPEUTICLINK();
        cdTherLink.setS(CDTHERAPEUTICLINKschemes.CD_THERAPEUTICLINKTYPE);
        cdTherLink.setSV("1.0");
        cdTherLink.setValue("gpconsultation");
        selectConsent.getCds().add(cdTherLink);

        selectConsent.getPatientsAndHcparties().add(createPatientIdType());
        selectConsent.getPatientsAndHcparties().add(createHcPartyIdProfessional());
        selectConsent.setBegindate(new DateTime());
        selectConsent.setEnddate(new DateTime());

        return selectConsent;
    }

    public Kmehrmessage createPutTransactionMessage() throws Exception {
        Kmehrmessage kmehrmessage = new Kmehrmessage();
        setHeaderTo(kmehrmessage);
        addTransactionTo(addFolderTo(kmehrmessage));

        return kmehrmessage;
    }

    public Kmehrmessage createPutTransactionMessageWithLocalId() throws Exception {
        Kmehrmessage kmehrmessage = new Kmehrmessage();
        setHeaderTo(kmehrmessage);
        addLocalIdTo(addTransactionTo(addFolderTo(kmehrmessage)));

        return kmehrmessage;
    }

    private void addLocalIdTo(TransactionType addedTransaction) throws TechnicalConnectorException {
        addedTransaction.getIds().add(createIdKmehr("EHBASICSOFT", "1.0", IdGeneratorFactory.getIdGenerator(
                IdGeneratorFactory.UUID).generateId(), IDKMEHRschemes.LOCAL));
    }

    private TransactionType addTransactionTo(FolderType folder) throws Exception {
        TransactionType transaction = createTransactionType(
                IdGeneratorFactory.getIdGenerator(
                        IdGeneratorFactory.UUID).generateId(),
                "<transaction xmlns='http://www.ehealth.fgov.be/standards/kmehr/schema/v1'><cd S='CD-TRANSACTION' SV='1.4'>sumehr</cd><date>2013-07-17</date><time>10:01:51+01:00</time><iscomplete>true</iscomplete><isvalidated>true</isvalidated><item><id S='ID-KMEHR' SV='1.0'>99999999.99999999<cd S='CD-ITEM' SV='1.4'>risk</cd><content><text L='fr'>travail sur écran</text></content><beginmoment><date>2013-06-21</date><time>14:51:24+01:00</time></beginmoment><recorddatetime>2013-06-21T14:53:28+02:00</recorddatetime></item><item><id S='ID-KMEHR' SV='1.0'>99999999.99999999<cd S='CD-ITEM' SV='1.4'>adr</cd><content><text L='fr'>Ticlopidine</text></content><beginmoment><date>2013-06-21</date><time>14:51:24+01:00</time></beginmoment><recorddatetime>2013-06-21T14:52:34+02:00</recorddatetime></item><item><id S='ID-KMEHR' SV='1.0'>99999999.99999999<cd S='CD-ITEM' SV='1.4'>medication</cd><content><cd S='CD-ATC' SV='1.0'>B01AC05</cd></content><content><text L='fr'>Ticlid (c) 250mg - 30 compr. enrobé(s)</text></content><content><medicinalproduct><intendedcd S='CD-DRUG-CNK' SV='2.0'>0857995</intendedcd><intendedname>Ticlid (c) 250mg - 30 compr. enrobé(s)</intendedname></medicinalproduct></content><beginmoment><date>2013-06-21</date></beginmoment><lifecycle><cd S='CD-LIFECYCLE' SV='1.3'>prescribed</cd></lifecycle><isrelevant>true</isrelevant><temporality><cd S='CD-TEMPORALITY' SV='1.0'>chronic</cd></temporality><quantity><decimal>1</decimal><unit><cd S='CD-UNIT' SV='1.3'>pkg</cd></unit></quantity><instructionforpatient L='fr'>1 compr. enrobé(s) 1 x / jour</instructionforpatient><recorddatetime>2013-06-21T14:51:24+02:00</recorddatetime></item><item><id S='ID-KMEHR' SV='1.0'>99999999.99999999<cd S='CD-ITEM' SV='1.4'>medication</cd><content><cd S='CD-ATC' SV='1.0'>C10AA07</cd></content><content><text L='fr'>rosuvastatine 40 mg - 98 compr. pelliculé(s)</text></content><content><medicinalproduct><intendedcd S='CD-DRUG-CNK' SV='2.0'>2055176</intendedcd><intendedname>rosuvastatine 40 mg - 98 compr. pelliculé(s)</intendedname></medicinalproduct></content><beginmoment><date>2013-06-21</date></beginmoment><endmoment><date>2013-09-27</date></endmoment><lifecycle><cd S='CD-LIFECYCLE' SV='1.3'>prescribed</cd></lifecycle><isrelevant>true</isrelevant><temporality><cd S='CD-TEMPORALITY' SV='1.0'>acute</cd></temporality><quantity><decimal>1</decimal><unit><cd S='CD-UNIT' SV='1.3'>pkg</cd></unit></quantity><instructionforpatient L='fr'>1 compr. 1 x / jour</instructionforpatient><recorddatetime>2013-06-21T14:51:24+02:00</recorddatetime></item><item><id S='ID-KMEHR' SV='1.0'>99999999.99999999<cd S='CD-ITEM' SV='1.4'>vaccine</cd><content><cd S='CD-VACCINEINDICATION' SV='1.0'>diphteria</cd><cd S='CD-VACCINEINDICATION' SV='1.0'>tetanus</cd><cd S='CD-ATC' SV='1.0'>J07AM51</cd></content><content><medicinalproduct><intendedcd S='CD-DRUG-CNK' SV='2.0'>1077593</intendedcd><intendedname>Tedivax pro adulto (c)</intendedname></medicinalproduct></content><beginmoment><date>2013-05-28</date></beginmoment><lifecycle><cd S='CD-LIFECYCLE' SV='1.3'>administrated</cd></lifecycle><recorddatetime>2013-06-21T14:53:34+02:00</recorddatetime></item><item><id S='ID-KMEHR' SV='1.0'>99999999.99999999<cd S='CD-ITEM' SV='1.4'>vaccine</cd><content><cd S='CD-VACCINEINDICATION' SV='1.0'>diphteria</cd><cd S='CD-VACCINEINDICATION' SV='1.0'>tetanus</cd><cd S='CD-VACCINEINDICATION' SV='1.0'>pertussis</cd><cd S='CD-VACCINEINDICATION' SV='1.0'>poliomyelitis</cd><cd S='CD-ATC' SV='1.0'>J07CA02</cd></content><content><medicinalproduct><intendedcd S='CD-DRUG-CNK' SV='2.0'>64429</intendedcd><intendedname>Boostrix Polio (c)</intendedname></medicinalproduct></content><beginmoment><date>2013-05-28</date></beginmoment><lifecycle><cd S='CD-LIFECYCLE' SV='1.3'>administrated</cd></lifecycle><recorddatetime>2013-06-21T14:53:34+02:00</recorddatetime></item></transaction>");
        folder.getTransactions().add(transaction);
        
        return transaction;
    }

    private FolderType addFolderTo(Kmehrmessage kmehrmessage) {
        FolderType folder = new FolderType();
        folder.getIds().add(createMessageId(HubConfigCommon.MESSAGE_ID));
        folder.setPatient(createPatientForTransaction());
        kmehrmessage.getFolders().add(folder);
        return folder;
    }

    private void setHeaderTo(Kmehrmessage kmehrmessage) throws Exception {
        kmehrmessage.setHeader(createHeader("20110701"));
    }


    public Kmehrmessage createTransactionSetMessage(org.imec.ivlab.core.model.patient.model.Patient patient, String startTransactionId) throws Exception {
        Map<String, Object> velocityContext = new HashMap<String, Object>();

        velocityContext.put("today", DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime()));
        velocityContext.put("timenow", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_TIME)); //
        velocityContext.put("idToday", DateTimeFormat.forPattern("yyyyMMdd").print(new DateTime()));
        velocityContext.put("startTransactionId", startTransactionId);


        String kmehr =  TemplateEngineUtils.generate(velocityContext , "/config/requests/" + testFilesLocation + "/" + TEMPLATE_FILE_PUT_TRANSACTION_SET);
        Kmehrmessage kmehrmessage = KmehrMarshaller.fromString(kmehr);

        PatientDataModifier modifier = new PatientDataModifier(patient);
        modifier.modify(kmehrmessage);

        return kmehrmessage;
    }

    public Kmehrmessage createTransactionMessage(org.imec.ivlab.core.model.patient.model.Patient patient, FolderType folderType) throws Exception {
        Map<String, Object> velocityContext = new HashMap<String, Object>();

        velocityContext.put("today", DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime()));
        velocityContext.put("timenow", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_TIME)); //
        velocityContext.put("idToday", DateTimeFormat.forPattern("yyyyMMdd").print(new DateTime()));


        String kmehr =  TemplateEngineUtils.generate(velocityContext , "/config/requests/" + testFilesLocation + "/" + TEMPLATE_FILE_PUT_TRANSACTION);
        Kmehrmessage kmehrmessage = KmehrMarshaller.fromString(kmehr);

        kmehrmessage.getFolders().clear();
        kmehrmessage.getFolders().add(folderType);

        PatientDataModifier modifier = new PatientDataModifier(patient);
        modifier.modify(kmehrmessage);

        return kmehrmessage;
    }

    /**
     * Create a transaction type needed for transaction operations
     */
    private TransactionType createTransactionType(String transactionId, String xml) throws Exception {
        TransactionType transaction = new MarshallerHelper<TransactionType, TransactionType>(TransactionType.class, TransactionType.class).toObject(xml);
        AuthorType author = new AuthorType();
        author.getHcparties().addAll(createSender().getHcparties());
        transaction.setAuthor(author);

        transaction.getIds().add(createIdKmehr(null, "1.0", transactionId, IDKMEHRschemes.ID_KMEHR));
        return transaction;
    }

    /**
     * Create a transaction type needed for transaction operations, with a LOCAL id
     */
    private TransactionType createTransactionTypeWithLocalId(String transactionId, String xml) throws Exception {
        TransactionType transaction = createTransactionType(transactionId, xml);
        transaction.getIds().add(createIdKmehr("EHBASICSOFT", "1.0", IdGeneratorFactory.getIdGenerator(
                IdGeneratorFactory.UUID).generateId(), IDKMEHRschemes.LOCAL));

        return transaction;
    }


    /**
     * Create a transactionWithPeriod type for transaction operations
     */
    public TransactionWithPeriodType createTransactionWithPeriodType(org.imec.ivlab.core.model.upload.TransactionType firstTransactionType, org.imec.ivlab.core.model.upload.TransactionType... otherTransactionTypes) {

        TransactionWithPeriodType transaction = new TransactionWithPeriodType();
        transaction.getCds().add(getCdtransaction("1.6", firstTransactionType.getTransactionTypeValueForGetTransactionList()));
        if (otherTransactionTypes.length > 0) {
            for (org.imec.ivlab.core.model.upload.TransactionType transactionType : otherTransactionTypes) {
                transaction.getCds().add(getCdtransaction("1.6", transactionType.getTransactionTypeValueForGetTransactionList()));
            }
        }
//        transaction.setBegindate(new DateTime().minusDays(0));
//        transaction.setEnddate(new DateTime().plusDays(1));
        return transaction;
    }

    public CDTRANSACTION getCdtransaction(String value, String transactionType) {
        CDTRANSACTION cdtransactionMedicationScheme = new CDTRANSACTION();
        cdtransactionMedicationScheme.setS(CDTRANSACTIONschemes.CD_TRANSACTION);
        cdtransactionMedicationScheme.setSV(value);
        cdtransactionMedicationScheme.setValue(transactionType);
        return cdtransactionMedicationScheme;
    }

    public CDTRANSACTION getCdtransaction(String version, CDTRANSACTIONschemes cdtransactionSchemes, String value) {
        CDTRANSACTION cdtransactionMedicationScheme = new CDTRANSACTION();
        cdtransactionMedicationScheme.setS(cdtransactionSchemes);
        cdtransactionMedicationScheme.setSV(version);
        cdtransactionMedicationScheme.setValue(value);
        return cdtransactionMedicationScheme;
    }

    /**
     * Create a transaction base type for transaction operations
     */
    public TransactionBaseType createTransactionBaseType(IDKMEHR idkmehr, HcpartyType authorHcParty) {
        TransactionBaseType transaction = new TransactionBaseType();
        transaction.setId(idkmehr);
        if (authorHcParty != null) {
            AuthorType authorType = new AuthorType();
            authorType.getHcparties().add(authorHcParty);
            transaction.setAuthor(authorType);
        }
        return transaction;
    }

    public SelectGetLatestUpdateType createSelectGetLatestUpdateType() {
        SelectGetLatestUpdateType selectGetLatestUpdateType = new SelectGetLatestUpdateType();
        Criteria criteria = new Criteria();
        criteria.setPatient(createPatientIdType());
        CDTRANSACTION cdtransaction = getCdtransaction("1.4", "medicationscheme");
        criteria.getCds().add(cdtransaction);
        selectGetLatestUpdateType.getCriterias().add(criteria);
        return selectGetLatestUpdateType;
    }

    public SelectGetLatestUpdateType createSelectGetLatestUpdateType(String patientId, String transactionType) {
        SelectGetLatestUpdateType selectGetLatestUpdateType = new SelectGetLatestUpdateType();
        Criteria criteria = new Criteria();
        criteria.setPatient(createPatientIdType(patientId));
        CDTRANSACTION cdtransaction = getCdtransaction("1.4", transactionType);
        criteria.getCds().add(cdtransaction);
        selectGetLatestUpdateType.getCriterias().add(criteria);
        return selectGetLatestUpdateType;
    }

    public SelectGetPatientAuditTrailType createGetPatientAuditTrailType(String patientId) {

        SelectGetPatientAuditTrailType selectGetPatientAuditTrailType = new SelectGetPatientAuditTrailType();
        selectGetPatientAuditTrailType.setPatient(createPatientIdType(patientId));
        selectGetPatientAuditTrailType.setSearchtype(LocalSearchType.GLOBAL);

        return selectGetPatientAuditTrailType;

    }

    public TransactionIdType createTransactionIdType(String transactionId) {
        TransactionIdType transaction = new TransactionIdType();
        transaction.getIds().add(createIdKmehr(null, "1.0", transactionId, IDKMEHRschemes.LOCAL));
        return transaction;
    }

    private AddressType createAddress() {
        AddressType address = new AddressType();
        address.setCity("");
        CDADDRESS cdAddress = new CDADDRESS();
        cdAddress.setS(CDADDRESSschemes.CD_ADDRESS);
        cdAddress.setSV("1.0");
        cdAddress.setValue("");
        address.getCds().add(cdAddress);
        CountryType country = new CountryType();
        CDCOUNTRY cdCountry = new CDCOUNTRY();
        cdCountry.setS(CDCOUNTRYschemes.CD_COUNTRY);
        cdCountry.setSV("1.0");
        cdCountry.setValue("");
        country.setCd(cdCountry);
        address.setCountry(country);
        address.setDistrict("");
        address.setHousenumber("");
        address.setNis("");
        address.setPostboxnumber("");
        address.setStreet("");
        address.setZip("");
        return address;
    }

    private TelecomType createTelecom() {
        TelecomType telecom = new TelecomType();
        telecom.setTelecomnumber("");
        CDTELECOM cdTelecom = new CDTELECOM();
        cdTelecom.setS(CDTELECOMschemes.CD_TELECOM);
        cdTelecom.setSV("1.0");
        cdTelecom.setValue("");
        telecom.getCds().add(cdTelecom);
        return telecom;
    }
}
