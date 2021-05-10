package org.imec.ivlab.core.model.upload.kmehrentrylist;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.imec.ivlab.core.kmehr.model.localid.URI;
import org.imec.ivlab.core.kmehr.model.localid.util.URIBuilder;

public class KmehrExtractor {

    private static Logger log = Logger.getLogger(KmehrExtractor.class);


    public static KmehrEntryList getKmehrEntryList(File file) {


        try {
            String fileContent = FileUtils.readFileToString(file, "UTF-8");
            return getKmehrEntryList(fileContent);
        } catch (IOException e) {
            log.error("Failed to read contents from file: " + file.getAbsolutePath(), e);
        }

        return new KmehrEntryList();

    }

    public static KmehrEntryList getKmehrEntryList(String fileContent) {

        KmehrEntryList kmehrEntryList = new KmehrEntryList();

        Pattern pattern = Pattern.compile("(?:(^\\/subject\\/\\d{11}\\/[a-zA-Z\\-]+\\/\\d{5}[\\/\\dnew]*).*?)?(<(?:ns\\d+:)?kmehrmessage.*?<\\/(?:ns\\d+:)?kmehrmessage>)", Pattern.DOTALL + Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);

        Matcher matcher = pattern.matcher(fileContent);

        while (matcher.find()) {

            URI uri = null;
            if (matcher.group(1) != null) {
                uri = URIBuilder.fromString(matcher.group(1));
            }

            kmehrEntryList.getKmehrEntries().add(new KmehrEntry(new BusinessData(matcher.group(2)), uri));
        }

        return kmehrEntryList;

    }

}
