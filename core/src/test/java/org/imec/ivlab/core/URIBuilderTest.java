package org.imec.ivlab.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.kmehr.model.localid.URI;
import org.imec.ivlab.core.kmehr.model.localid.util.URIBuilder;
import org.imec.ivlab.core.kmehr.model.localid.util.URIConverter;
import org.imec.ivlab.core.kmehr.model.localid.util.URIWriter;

import org.testng.annotations.Test;

public class URIBuilderTest {
    
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

}
