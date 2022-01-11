package org.imec.ivlab.core.kmehr.model.localid.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.kmehr.model.localid.URI;
import org.imec.ivlab.core.kmehr.model.localid.URIType;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.junit.Test;

public class URIBuilder {

    private final static Logger log = LogManager.getLogger(URIBuilder.class);

    @Test
    public void testParseString(){
        URI uri1 = URIBuilder.fromString("/subject/90010100101/medication-scheme/47190");
        log.info(uri1);
        log.info(URIWriter.toString(uri1));
        URI uri2 = (URIBuilder.fromString("/subject/90010100101/medication-scheme/47190/1"));
        log.info(uri2);
        log.info(URIWriter.toString(uri2));
        log.info("URI for update: " + URIWriter.toString(URIConverter.convertToUpdate(uri2)));
        URI uri3 = (URIBuilder.fromString("/subject/90010100101/medication-scheme/47190/new/1"));
        log.info(uri3);
        log.info(URIWriter.toString(uri3));
        URI uri4 = (URIBuilder.fromString("/subject/90010100101/medication-scheme/new"));
        log.info(uri4);
        log.info(URIWriter.toString(uri4));
        URI uri5 = (URIBuilder.fromString("/subject/90010100101/vaccination/B1BFB33C7CD24729A866A869F44CA9F8/1"));
        log.info(uri5);
        log.info(URIWriter.toString(uri5));
    }

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
