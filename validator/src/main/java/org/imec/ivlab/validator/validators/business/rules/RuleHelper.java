package org.imec.ivlab.validator.validators.business.rules;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class RuleHelper {

    public static File getResource(String relativePath) {

        URL resource = RuleHelper.class.getClassLoader().getResource(relativePath);

        if (resource == null) {
            throw new RuntimeException("Couldn't find file at " + relativePath);
        }

        try {
            return new File(resource.toURI().toString().replace("file:", ""));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error opening file at: " + resource.toString(), e);
        }

    }


}
