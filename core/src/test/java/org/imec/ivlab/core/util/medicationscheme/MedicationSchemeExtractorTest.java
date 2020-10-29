package org.imec.ivlab.core.util.medicationscheme;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import org.apache.commons.collections.CollectionUtils;
import org.imec.ivlab.core.TestUtil;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MedicationSchemeExtractor;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.testng.Assert.assertTrue;

public class MedicationSchemeExtractorTest {

    @Test
    public void testGetMedicationSchemeEntriesConnectorFormatWithURIS() throws Exception {

        KmehrEntryList kmehrEntryList = TestUtil.getKmehrEntryList("10-kmehrs-10-uris.txt");
        MSEntryList medicationSchemeEntries = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrEntryList);
        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries()), equalTo(10));
        for (MSEntry msEntry : medicationSchemeEntries.getMsEntries()) {
            assertThat(CollectionUtils.size(msEntry.getTsTransactions()), equalTo(0));
        }

        ensureRegimenOrPosologyCorrectlyUnMarshalled(medicationSchemeEntries);

    }

    @Test
    public void testGetMedicationSchemeEntriesConnectorFormatWithoutURIS() throws Exception {

        KmehrEntryList kmehrEntryList = TestUtil.getKmehrEntryList("10-kmehrs-no-uris.txt");
        MSEntryList medicationSchemeEntries = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrEntryList);
        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries()), equalTo(10));
        for (MSEntry msEntry : medicationSchemeEntries.getMsEntries()) {
            assertThat(CollectionUtils.size(msEntry.getTsTransactions()), equalTo(0));

        }

        ensureRegimenOrPosologyCorrectlyUnMarshalled(medicationSchemeEntries);

    }

    @Test
    public void testGetMedicationSchemeEntriesGatewayFormat() throws Exception {

        KmehrEntryList kmehrEntryList = TestUtil.getKmehrEntryList("3-kmehrs-gateway-format.txt");
        MSEntryList medicationSchemeEntries = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrEntryList);
        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries()), equalTo(5));

        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries().get(0).getTsTransactions()), equalTo(0));
        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries().get(1).getTsTransactions()), equalTo(0));

        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries().get(2).getTsTransactions()), equalTo(1));
        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries().get(3).getTsTransactions()), equalTo(0));

        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries().get(4).getTsTransactions()), equalTo(2));

        ensureRegimenOrPosologyCorrectlyUnMarshalled(medicationSchemeEntries);


    }

    private void ensureRegimenOrPosologyCorrectlyUnMarshalled(MSEntryList msEntryList) {
        if (msEntryList == null || CollectionsUtil.emptyOrNull(msEntryList.getMsEntries())) {
            return;
        }

        for (MSEntry msEntry : msEntryList.getMsEntries()) {
            for (ItemType itemType : TransactionUtil.getItems(msEntry.getMseTransaction(), CDITEMvalues.MEDICATION)) {
                if (itemType.getPosology() == null) {
                    assertTrue(itemType.getRegimen().getDaynumbersAndQuantitiesAndDates() != null &&
                    itemType.getRegimen().getDaynumbersAndQuantitiesAndDates().size() > 0, "A kmehrmessage object has neither a posology nor regimen. Probably something went wrong during unmarshalling. ");
                }
            }
        }
    }

    @Test
    public void testGetMedicationSchemeEntriesMixConnectorAndGatewayFormat() throws Exception {

        KmehrEntryList kmehrEntryList = TestUtil.getKmehrEntryList("3-kmehrs-gateway-format-and-2-kmehrs-connector-format.txt");
        MSEntryList medicationSchemeEntries = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrEntryList);
        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries()), equalTo(7));

        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries().get(0).getTsTransactions()), equalTo(0));
        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries().get(1).getTsTransactions()), equalTo(0));

        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries().get(2).getTsTransactions()), equalTo(1));

        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries().get(3).getTsTransactions()), equalTo(1));
        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries().get(4).getTsTransactions()), equalTo(0));

        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries().get(5).getTsTransactions()), equalTo(0));

        assertThat(CollectionUtils.size(medicationSchemeEntries.getMsEntries().get(6).getTsTransactions()), equalTo(2));

    }


}