package org.imec.ivlab.core.model.internal.parser.sumehr.SumehrMapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.imec.ivlab.core.TestUtil;
import org.imec.ivlab.core.model.internal.parser.sumehr.Sumehr;
import org.imec.ivlab.core.model.internal.parser.sumehr.mapper.SumehrMapper;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.extractor.SumehrListExtractor;
import org.testng.annotations.Test;

public class SumehrMapperTest {

    @Test
    public void testKmehrToSumehr() {

        KmehrEntryList kmehrEntryList = TestUtil.getKmehrEntryList("2-map-kmehr-to-sumehr.txt");
        KmehrWithReferenceList sumehrList = new SumehrListExtractor().getKmehrWithReferenceList(kmehrEntryList);

        Sumehr sumehr = SumehrMapper.kmehrToSumehr(sumehrList.getList().get(0).getKmehrMessage());

        assertThat(sumehr.getMedicationEntries()).hasSize(4);
        assertThat(sumehr.getHealthCareElements()).hasSize(6);

    }

}