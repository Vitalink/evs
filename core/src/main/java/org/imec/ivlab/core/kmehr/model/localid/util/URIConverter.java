package org.imec.ivlab.core.kmehr.model.localid.util;

import org.apache.log4j.Logger;
import org.imec.ivlab.core.kmehr.model.localid.URI;
import org.imec.ivlab.core.kmehr.model.localid.URIType;

public class URIConverter {

    private final static Logger log = Logger.getLogger(URIConverter.class);


    public static URI convertToUpdate(URI uri) {

        if (URIType.UPDATE.equals(uri.getUriType())) {
            throw new RuntimeException("URI is already for update. Will not change it: " + uri);
        }

        URI newUri = new URI();

        if (uri.getSubjectId() == null) {
            throw new RuntimeException("Cannot convert URI to update URI because subject id is missing: " + uri);
        }

        newUri.setSubjectId(uri.getSubjectId());

        if (uri.getTransactionType() == null) {
            throw new RuntimeException("Cannot convert URI to update URI because transaction type is missing: " + uri);
        }

        newUri.setTransactionType(uri.getTransactionType());


        if (uri.getTransactionId() == null) {
            throw new RuntimeException("Cannot convert URI to update URI because medication id is missing: " + uri);
        }

        newUri.setTransactionId(uri.getTransactionId());

        if (uri.getTransactionVersion() == null) {
            throw new RuntimeException("Cannot convert URI to update URI because transaction version is missing: " + uri);
        }

        newUri.setTransactionVersion(uri.getTransactionVersion());

        newUri.setUriType(URIType.UPDATE);

        return newUri;

    }


}
