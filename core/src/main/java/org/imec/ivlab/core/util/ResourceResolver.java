package org.imec.ivlab.core.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ResourceResolver {

    private final static Logger log = Logger.getLogger(ResourceResolver.class);


    public static File getProjectDirectoryAsFile(String relativeProjectDirectory) {

        // found resource outside jar, for instance during development
        File fileAsExternalResource = getProjectDirectoryNonJarMode(relativeProjectDirectory);

        if (fileAsExternalResource != null && fileAsExternalResource.exists()) {
            log.info("Found resource: " + fileAsExternalResource.getAbsolutePath());
            return fileAsExternalResource;
        }

        // found resource inside jar
        File fileAsInternalResource = getProjectDirectoryJarMode(relativeProjectDirectory);
        if (fileAsInternalResource != null && fileAsInternalResource.exists()) {
            log.info("Running as jar. Found resource: " + fileAsExternalResource.getAbsolutePath());
            return fileAsInternalResource;
        }

        log.error("Couldn't find resource: " + relativeProjectDirectory);
        return null;

    }

    private static File getProjectDirectoryNonJarMode(String relativePathToResource) {

        URL classLocation = ResourceResolver.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            File file = new File(Paths.get(classLocation.toURI()).toFile() + File.separator + ".." + File.separator + ".." + File.separator + relativePathToResource);
            log.trace("Running from IDE. Found (" + relativePathToResource + "): " + file.getAbsolutePath());
            return file;
        } catch (URISyntaxException e) {
            log.error("Error when converting class location to URI for relative path: " + relativePathToResource, e);
        }

        return null;

    }

    private static File getProjectDirectoryJarMode(String relativePathToResource) {

        String classLocation = ResourceResolver.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String classLocationParent = new File(classLocation).getParent();

        try {
            File file = new File(Paths.get(URLDecoder.decode(classLocationParent, "UTF-8")).toFile()  + File.separator + relativePathToResource);
            log.trace("Running as jar. Found (" + relativePathToResource + "): " + file.getAbsolutePath());
            return file;
        } catch (UnsupportedEncodingException e) {
            log.error("Error when URL decoding URL: " + classLocationParent, e);
        }

        return null;

    }

    public static List<String> getMatchingJarEntries(String startsWith) {

        log.trace("starts with: " + startsWith);

        List<String> jarEntries = new ArrayList<>();
        try {
            File file = new File(ResourceResolver.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                log.trace("Checking if: " + jarEntry.getName() + " starts with " + startsWith);
                if (org.apache.commons.lang3.StringUtils.startsWithIgnoreCase(jarEntry.getName(), startsWith)) {
                    jarEntries.add(jarEntry.getName());
                    log.trace("Found jar entry: " + jarEntry.getName());
                }
            }
            return jarEntries;
        } catch (URISyntaxException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);

        }

        return null;
    }

}
