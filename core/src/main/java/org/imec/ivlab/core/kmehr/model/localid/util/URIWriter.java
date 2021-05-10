package org.imec.ivlab.core.kmehr.model.localid.util;


import org.imec.ivlab.core.kmehr.model.localid.URI;
import org.imec.ivlab.core.kmehr.model.localid.URIType;

public class URIWriter {

    public static String toString(URI uri) {

        StringBuffer sb = new StringBuffer();

        sb.append("/subject/");
        sb.append(uri.getSubjectId());
        sb.append("/");
        sb.append(uri.getTransactionType());

        if (URIType.NEW.equals(uri.getUriType())) {
            sb.append("/new");
        } else if (URIType.UPDATE.equals(uri.getUriType())) {
            sb.append("/");
            sb.append(uri.getTransactionId());
            sb.append("/new/");
            sb.append(uri.getTransactionVersion());
        } else if (URIType.REGULAR.equals(uri.getUriType())) {
            sb.append("/");
            sb.append(uri.getTransactionId());
            sb.append("/");
            sb.append(uri.getTransactionVersion());
        }

        return sb.toString();

    }

}
