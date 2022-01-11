package org.imec.ivlab.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class TestResourceReader {

    private final static Logger LOG = LogManager.getLogger(TestResourceReader.class);


    private static final String TEMPLATE_FOLDER = "testkmehrs";

    public static String read(String templateName) {

        try {

            InputStream resource = TestResourceReader.class.getClassLoader().getResourceAsStream(TEMPLATE_FOLDER + "/" + templateName);

            if (resource == null) {
                throw new RuntimeException("Couldn't find template with name " + templateName);
            }

            LOG.info("Reading file at location: " + TEMPLATE_FOLDER + "/" + templateName);
            return IOUtils.toString(resource, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read kmehr template " + templateName, e);
        }

    }

}
