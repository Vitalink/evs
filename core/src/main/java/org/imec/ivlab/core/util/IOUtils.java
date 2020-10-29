package org.imec.ivlab.core.util;

import org.apache.log4j.Logger;
import org.imec.ivlab.core.exceptions.ResourceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class IOUtils {

    private final static Logger log = Logger.getLogger(IOUtils.class);

    public static String getResourceAsString(String location) {
        return convertStreamToString(getResourceAsStream(location));
    }

    public static File getResourceAsFile(String location) {
        return getResourceAsFile(location, UUID.randomUUID().toString());
    }

    public static File getResourceAsFile(String location, String suffix) {
        InputStream inputStream = null;

        File var3;
        try {
            inputStream = getResourceAsStream(location);
            File e = File.createTempFile(UUID.randomUUID().toString(), suffix);
            e.deleteOnExit();
            org.apache.commons.io.IOUtils.copy(inputStream, new FileOutputStream(e));
            var3 = e;
        } catch (IOException e) {
            log.error(e.getClass().getSimpleName() + ": " + e.getMessage());
            throw new ResourceException("Failed to copy resource to temp file", e);
        } catch (Exception e) {
            log.error(e);
            throw e;
        } finally {
            closeQuietly(inputStream);
        }

        return var3;
    }


    public static File getFile(ClassLoader classLoader, String pathRelativeToResourcesFolder) {

        URL resource = classLoader.getResource(pathRelativeToResourcesFolder);

        if (resource == null) {
            throw new RuntimeException("Couldn't find file at " + pathRelativeToResourcesFolder);
        }

        try {
            return new File(resource.toURI().toString().replace("file:", ""));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error opening file at: " + resource.toString(), e);
        }


    }

    public static InputStream getResourceAsStream(String location) {

        if(location == null) {
            throw new IllegalArgumentException("Location should not be null");
        } else {
            log.debug("Loading " + location + " as ResourceAsStream");
            Object stream = IOUtils.class.getResourceAsStream(location);
            if(stream == null) {
                File file = new File(location);
                if(!file.exists()) {
                    try {
                        URL e = new URL(location);
                        return e.openStream();
                    } catch (Exception var5) {
                        throw new ResourceException("location [" + location + "] could not be retrieved as URL, classpath resource or file.");
                    }
                }

                try {
                    stream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    throw new ResourceException("Couldn't read file at: " + file.getAbsolutePath());
                }
            }

            return (InputStream)stream;
        }
    }


    public static String convertStreamToString(InputStream is) {
        if(is == null) {
            throw new IllegalArgumentException("inputstream should not be null");
        } else {
            String result;
            try {
                result = org.apache.commons.io.IOUtils.toString(is, "UTF-8");
            } catch (IOException e) {
                throw new ResourceException("Inputstream could not be read to String", e);
            } finally {
                closeQuietly(is);
            }

            return result;
        }
    }


}
