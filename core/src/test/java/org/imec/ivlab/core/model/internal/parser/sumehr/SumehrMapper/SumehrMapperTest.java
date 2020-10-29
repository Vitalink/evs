package org.imec.ivlab.core.model.internal.parser.sumehr.SumehrMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import org.apache.log4j.Logger;
import org.imec.ivlab.core.TestUtil;
import org.imec.ivlab.core.model.internal.parser.sumehr.Sumehr;
import org.imec.ivlab.core.model.internal.parser.sumehr.mapper.SumehrMapper;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.sumehrlist.SumehrList;
import org.imec.ivlab.core.model.upload.sumehrlist.SumehrListExtractor;
import org.testng.annotations.Test;

public class SumehrMapperTest {

    private final static Logger LOG = Logger.getLogger(SumehrMapperTest.class);


    @Test
    public void testKmehrToSumehr() throws Exception {

        KmehrEntryList kmehrEntryList = TestUtil.getKmehrEntryList("2-map-kmehr-to-sumehr.txt");
        SumehrList sumehrList = SumehrListExtractor.getSumehrList(kmehrEntryList);

        Sumehr sumehr = SumehrMapper.kmehrToSumehr(sumehrList.getList().get(0).getKmehrMessage());

        assertThat(sumehr.getMedicationEntries(), hasSize(4));
        assertThat(sumehr.getHealthCareElements(), hasSize(6));

    }

//    @Test
//    public void testToHealthCareElementFillsUnparsed() throws ParserConfigurationException, TransformerException, SAXException, IOException {
//
//        ItemType itemType = TestUtil.filecontentToObject(ItemType.class, "50-healthcareElement-with-unparsed-fields.xml");
//        HealthCareElement healthCareElement = SumehrMapper.toHealthCareElement(itemType);
//        assertThat(healthCareElement.getUnparsedAsString(), is(notNullValue()));
//
//    }

//    @Test
//    public void testToHealthCareElementAllParsed() throws ParserConfigurationException, TransformerException, SAXException, IOException {
//
//        ItemType itemType = TestUtil.filecontentToObject(ItemType.class, "51-healthcareElement-all-parsable.xml");
//        HealthCareElement healthCareElement = SumehrMapper.toHealthCareElement(itemType);
//        assertThat(healthCareElement.getUnparsedAsString(), is(nullValue()));
//
//    }

//    @Test
//    public void testToRiskFillsUnparsed() throws ParserConfigurationException, TransformerException, SAXException, IOException {
//
//        ItemType itemType = TestUtil.filecontentToObject(ItemType.class, "52-risk-with-unparsed-fields.xml");
//        Risk risk = SumehrMapper.toRisk(itemType);
//        assertThat(risk.getUnparsedAsString(), is(notNullValue()));
//
//    }

//    @Test
//    public void testToRiskAllParsed() throws ParserConfigurationException, TransformerException, SAXException, IOException {
//
//        ItemType itemType = TestUtil.filecontentToObject(ItemType.class, "53-risk-all-parsable.xml");
//        Risk risk = SumehrMapper.toRisk(itemType);
//        assertThat(risk.getUnparsedAsString(), is(nullValue()));
//
//    }

//    @Test
//    public void testToVaccinationAllParsed() throws ParserConfigurationException, TransformerException, SAXException, IOException {
//
//        ItemType itemType = TestUtil.filecontentToObject(ItemType.class, "55-vaccination-all-parsable.xml");
//        Vaccination vaccination = SumehrMapper.toVaccination(itemType);
//        assertThat(vaccination.getUnparsedAsString(), is(nullValue()));
//
//    }

//    @Test
//    public void testToPersonAllParsed() throws ParserConfigurationException, TransformerException, SAXException, IOException {
//
//        ItemType itemType = TestUtil.filecontentToObject(ItemType.class, "59-patientwill-all-parsable.xml");
//        PatientWill patientWill = SumehrMapper.toPatientWill(itemType);
//        assertThat(patientWill.getUnparsedAsString(), is(nullValue()));
//
//    }

//    @Test
//    public void testToMedicationItemAllParsed() throws ParserConfigurationException, TransformerException, SAXException, IOException {
//
//        ItemType itemType = TestUtil.filecontentToObject(ItemType.class, "61-medication-all-parsable.xml");
//        MedicationEntrySumehr medicationEntrySumehr = SumehrMapper.toMedicationItem(itemType);
//        assertThat(medicationEntrySumehr.getUnparsedAsString(), is(nullValue()));
//
//    }

//    @Test
//    public void testToAuthorAllParsed() throws ParserConfigurationException, TransformerException, SAXException, IOException {
//
//        HcpartyType hcpartyType = TestUtil.filecontentToObject(HcpartyType.class, "63-author-all-parsable.xml");
//        HcParty hcParty = SumehrMapper.toHcParty(hcpartyType);
//        assertThat(hcParty.getUnparsedAsString(), is(nullValue()));
//
//    }

//    @Test
//    public void testToSumehrFillsUnparsed() throws ParserConfigurationException, TransformerException, SAXException, IOException {
//
//        FolderType folderType = TestUtil.filecontentToObject(FolderType.class, "64-sumehr-with-unparsed-content.xml");
//        Sumehr sumehr = SumehrMapper.folderToSumehr(folderType);
//        assertThat(sumehr.getUnparsedAsString(), is(notNullValue()));
//
//    }

//    @Test
//    public void testToSumehrAllParsed() throws ParserConfigurationException, TransformerException, SAXException, IOException {
//
//        FolderType folderType = TestUtil.filecontentToObject(FolderType.class, "65-sumehr-all-items-parsable.xml");
//        Sumehr sumehr = SumehrMapper.folderToSumehr(folderType);
//        assertThat(sumehr.getUnparsedAsString(), is(nullValue()));
//
//    }

}