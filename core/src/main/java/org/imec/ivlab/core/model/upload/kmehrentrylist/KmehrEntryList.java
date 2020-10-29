package org.imec.ivlab.core.model.upload.kmehrentrylist;

import java.util.ArrayList;
import java.util.List;
import org.imec.ivlab.core.kmehr.model.localid.URI;

public class KmehrEntryList {

    private List<KmehrEntry> kmehrEntries = new ArrayList<>();

    public List<BusinessData> getBusinessDataList() {

        List<BusinessData> businessDataList = new ArrayList<>();

        if (kmehrEntries == null) {
            return businessDataList;
        }

        for (KmehrEntry kmehrEntry : kmehrEntries) {
            businessDataList.add(kmehrEntry.getBusinessData());
        }

        return businessDataList;

    }

    public List<URI> getURIList() {

        List<URI> URIList = new ArrayList<>();

        if (kmehrEntries == null) {
            return URIList;
        }

        for (KmehrEntry kmehrEntry : kmehrEntries) {
            URIList.add(kmehrEntry.getUri());
        }

        return URIList;

    }

    public List<KmehrEntry> getKmehrEntries() {
        return kmehrEntries;
    }

    public void setKmehrEntries(List<KmehrEntry> kmehrEntries) {
        this.kmehrEntries = kmehrEntries;
    }



}
