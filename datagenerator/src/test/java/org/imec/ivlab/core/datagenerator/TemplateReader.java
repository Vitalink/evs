package org.imec.ivlab.core.datagenerator;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class TemplateReader {


    private static final String TEMPLATE_FOLDER = "testkmehrs";

    public static String read(String templateName) {

        try {
            URL resource = TemplateReader.class.getClassLoader().getResource(TEMPLATE_FOLDER + "/" + templateName);

            if (resource == null) {
                throw new RuntimeException("Couldn't find template with name " + templateName);
            }

            File file = null;

            file = new File(resource.toURI().toString().replace("file:", ""));

            return FileUtils.readFileToString(file, "UTF-8");
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Failed tso read kmehr template " + templateName, e);
        }

    }

}
