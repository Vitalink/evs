package org.imec.ivlab.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class TemplateReader {


    private static final String TEMPLATE_FOLDER = "example_kmehrs";

    public static String read(String templateName) {

        try {
            URL resource = TemplateReader.class.getClassLoader().getResource(TEMPLATE_FOLDER + "/"+ templateName);

            if (resource == null) {
                throw new RuntimeException("Couldn't find template with name " + templateName);
            }


            return FileUtils.readFileToString(new File(resource.toURI().toString().replace("file:", "")), "UTF-8");

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Failed to read kmehr template " + templateName, e);
        }

    }

}
