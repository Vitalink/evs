package be.ehealth.businessconnector.testcommons.utils;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author EHP
 */
public class FileTestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FileTestUtils.class);


    /**
     * Creates a new temp file and writes a String to it, given some a file name prefix.
     * Once all the content has been written to the temp file, 
     * the file is renamed with the file name extension supplied.
     * The file name generated is unique within a test suite execution.
     * 
     * @param content the String content to be written to the file
     * @param fileNamePrefix the file name prefix, base for the file name.
     * @param fileNameExtension the file name extension for the final file name (e.g., <code>".xml"</code>)
     * 
     * @throws IOException
     */
    public static void writeToFile(String content, String fileNamePrefix, String fileNameExtension) throws IOException {
        Validate.notEmpty(fileNamePrefix);
        Validate.notEmpty(fileNameExtension);

        String userHome = System.getProperty("user.home");
        if (userHome == null) {
            throw new IllegalStateException("user.home==null");
        }
        File home = new File(userHome);
        File sendFilesDir = new File(home, "invoicingSendFiles");
        sendFilesDir.mkdir();
        java.io.File contentFile = File.createTempFile(fileNamePrefix, fileNameExtension, sendFilesDir);
        contentFile.createNewFile();
        LOG.debug("writing content to file on location " + contentFile.getAbsolutePath() + " : " + new String(content));
        FileWriter writer = new FileWriter(contentFile);
        writer.write(new String(content));
        writer.flush();
        writer.close();
    }
}
