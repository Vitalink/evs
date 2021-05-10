package org.imec.ivlab.core.util.kmehr;

import org.apache.commons.collections.CollectionUtils;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.TestUtil;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@org.testng.annotations.Test
public class KmehrExtractorTest {


    @Test
    public void testGetKmehrEntryListWithURIs() {

        KmehrEntryList kmehrEntryList = TestUtil.getKmehrEntryList("10-kmehrs-10-uris.txt");
        assertThat(CollectionUtils.size(removeNullValues(kmehrEntryList.getBusinessDataList())), equalTo(10));
        assertThat(CollectionUtils.size(removeNullValues(kmehrEntryList.getURIList())), equalTo(10));

    }

    @Test
    public void testGetKmehrEntryListWithURIsKmehrsNotAfterNewline() {

        KmehrEntryList kmehrEntryList = TestUtil.getKmehrEntryList("10-kmehrs-10-uris-and-kmehrs-not-after-newline.txt");
        assertThat(CollectionUtils.size(removeNullValues(kmehrEntryList.getBusinessDataList())), equalTo(10));
        assertThat(CollectionUtils.size(removeNullValues(kmehrEntryList.getURIList())), equalTo(10));

    }

    @Test
    public void testGetKmehrEntryListWithoutURIs() {

        KmehrEntryList kmehrEntryList = TestUtil.getKmehrEntryList("10-kmehrs-no-uris.txt");
        assertThat(CollectionUtils.size(removeNullValues(kmehrEntryList.getBusinessDataList())), equalTo(10));
        assertThat(CollectionUtils.size(removeNullValues(kmehrEntryList.getURIList())), equalTo(0));

    }

    @Test
    public void testGetKmehrEntryListWithoutURIsKmehrsNotAfterNewline() {

        KmehrEntryList kmehrEntryList = TestUtil.getKmehrEntryList("10-kmehrs-no-uris-and-kmehrs-not-after-newline.txt");
        assertThat(CollectionUtils.size(removeNullValues(kmehrEntryList.getBusinessDataList())), equalTo(10));
        assertThat(CollectionUtils.size(removeNullValues(kmehrEntryList.getURIList())), equalTo(0));

    }

    private <T> Collection<T> removeNullValues(Collection<T> collection) {
        if (collection == null) {
            return null;
        }
        collection.removeAll(Collections.singleton(null));
        return collection;
    }


}
