package org.imec.ivlab.core.kmehr.model.localid.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.localid.URI;
import org.imec.ivlab.core.kmehr.model.localid.URIType;
import org.imec.ivlab.core.model.upload.TransactionType;

public class URIBuilder {

    public static URI forTransaction(String subjectID, TransactionType transactionType) {

        URI uri = new URI();
        uri.setSubjectId(subjectID);
        uri.setTransactionType(transactionType.getTransactionTypeValueForGetTransactionList());
        return uri;

    }

    public static URI fromString(String uriString) {

        String patternForExistingOrUpdate = "(\\d{11})\\/([\\w-]+)\\/(\\d{5}|[\\d\\w]{32})(?:\\/(new|\\d{5}))?(?:\\/(\\d+))?";
        Pattern r = Pattern.compile(patternForExistingOrUpdate);
        Matcher m = r.matcher(uriString);

        while (m.find()) {
            URI uri = new URI();
            uri.setSubjectId(m.group(1));
            uri.setTransactionType(m.group(2));
            uri.setTransactionId(m.group(3));
            if (StringUtils.equalsIgnoreCase(m.group(4), "new")) {
                uri.setUriType(URIType.UPDATE);
            } else {
                uri.setUriType(URIType.REGULAR);
            }
            if (StringUtils.isNotEmpty(m.group(5))) {
                uri.setTransactionVersion(Integer.valueOf(m.group(5)));
            }

            return uri;
        }

        String patternForNew = "(\\d{11})\\/([\\w-]+)\\/new";
        r = Pattern.compile(patternForNew);
        m = r.matcher(uriString);

        while (m.find()) {
            URI uri = new URI();
            uri.setSubjectId(m.group(1));
            uri.setTransactionType(m.group(2));
            uri.setUriType(URIType.NEW);

            return uri;
        }

        throw new RuntimeException("Failed to parse uristring into URI. uriString: " + uriString);

    }

}
