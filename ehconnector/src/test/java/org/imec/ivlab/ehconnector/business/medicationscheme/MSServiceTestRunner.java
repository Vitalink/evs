package org.imec.ivlab.ehconnector.business.medicationscheme;

import org.imec.ivlab.core.data.PatientKey;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.model.upload.msentrylist.MedicationSchemeExtractor;
import org.imec.ivlab.core.model.patient.PatientReader;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.ehconnector.business.HubHelper;

import static org.imec.ivlab.core.authentication.AuthenticationConfigReader.GP_PETERS;
import static org.imec.ivlab.core.authentication.AuthenticationConfigReader.GP_VAN_GUCHT;
import static org.imec.ivlab.core.authentication.AuthenticationConfigReader.PH_PETERS;
import static org.imec.ivlab.core.authentication.AuthenticationConfigReader.loadByName;

public class MSServiceTestRunner {

    public static void main(String[] args) throws Exception {
        MSServiceTestRunner msServiceTestRunner = new MSServiceTestRunner();
        msServiceTestRunner.testPut();
    }

    public void testPut() throws Exception {

        MSServiceImpl gatewayDAO = new MSServiceImpl();

        String kmehrFileContent = IOUtils.getResourceAsString("/kmehrs/test-put.xml");

        Patient patient = PatientReader.loadPatientByKey(PatientKey.STEVEN);

        gatewayDAO.authenticate(loadByName(GP_VAN_GUCHT));
        KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(kmehrFileContent);
        MSEntryList msEntryList = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrEntryList);
        HubHelper hubHelper = new HubHelper();
        gatewayDAO.putMedicationScheme(patient, hubHelper.createTransactionSetMessage(patient, "1000"),msEntryList);


    }

    public void testGet() throws Exception {

        MSServiceImpl gatewayDAO = new MSServiceImpl();

        Patient patient = PatientReader.loadPatientByKey(PatientKey.STEVEN);
        gatewayDAO.authenticate(loadByName(GP_VAN_GUCHT));
        gatewayDAO.getMedicationScheme(patient);


    }

    public void testReplacePutMetSuspensions() throws Exception {

        MSServiceImpl gatewayDAO = new MSServiceImpl();

        String kmehrFileContent = IOUtils.getResourceAsString("/kmehrs/example-kmehrs-2-met-suspensions.xml");

        Patient patient = PatientReader.loadPatientByKey(PatientKey.STEVEN);

        gatewayDAO.authenticate(loadByName(GP_PETERS));
        gatewayDAO.authenticate(loadByName(PH_PETERS));
        gatewayDAO.authenticate(loadByName(GP_VAN_GUCHT));
        gatewayDAO.authenticate(loadByName(GP_PETERS));
        KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(kmehrFileContent);
        MSEntryList msEntryList = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrEntryList);
        HubHelper hubHelper = new HubHelper();
        gatewayDAO.putMedicationScheme(patient, hubHelper.createTransactionSetMessage(patient, "1000"),msEntryList);



    }

    public void testAddPutMetSuspensions() throws Exception {

        MSServiceImpl gatewayDAO = new MSServiceImpl();

        Patient patient = PatientReader.loadPatientByKey(PatientKey.STEVEN);

        String kmehrFileContent = IOUtils.getResourceAsString("/kmehrs/example-kmehrs-2-met-suspensions.xml");

        gatewayDAO.authenticate(loadByName(GP_PETERS));
        KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(kmehrFileContent);
        MSEntryList msEntryList = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrEntryList);
        HubHelper hubHelper = new HubHelper();
        gatewayDAO.putMedicationScheme(patient, hubHelper.createTransactionSetMessage(patient, "1000"),msEntryList);



    }

}
