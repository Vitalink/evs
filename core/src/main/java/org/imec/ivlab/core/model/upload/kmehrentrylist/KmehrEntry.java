package org.imec.ivlab.core.model.upload.kmehrentrylist;

import org.imec.ivlab.core.kmehr.model.localid.URI;

public class KmehrEntry {

    private BusinessData businessData;
    private URI uri;

    public BusinessData getBusinessData() {
        return businessData;
    }

    public void setBusinessData(BusinessData businessData) {
        this.businessData = businessData;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public KmehrEntry(BusinessData businessData, URI uri) {
        this.businessData = businessData;
        this.uri = uri;
    }
}
