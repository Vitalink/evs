package org.imec.ivlab.core.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.StandardOpenOption;

public class FileUtil {

    private final static Logger log = LogManager.getLogger(FileUtil.class);


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try(FileInputStream source = new FileInputStream(sourceFile)) {
            try(FileOutputStream destination = new FileOutputStream(destFile)) {
                FileChannel sourceChannel = source.getChannel();
                destination.getChannel().transferFrom(sourceChannel, 0, sourceChannel.size());
            }
        }
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { // some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void createDirectoriesRecursively(String directoryPath) {
        File file = new File(directoryPath);
        if (!file.exists()) {
            boolean retval = file.mkdirs();

            if (!retval) {
                throw new RuntimeException("Failed to create directories: " + directoryPath);
            }
        }
    }

    public static boolean isLocked(File file) {

        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            FileLock lock = channel.tryLock();
            if (lock != null) {
                lock.release(); // Release the lock immediately
                return false; // File is not locked
            } else {
                return true; // File is locked
            }
        } catch (OverlappingFileLockException e) {
            return true;
        } catch (IOException e) {
            // Handle any exceptions
            log.error("Error when checking if file is locked: ", e);
        }

        return false; // Unable to determine lock status, assuming not locked
}

    public static File appendTextToFilename(File file, String textToAppend) {
        return new File(FilenameUtils.getFullPath(file.getAbsolutePath()) + FilenameUtils.getBaseName(file.getAbsolutePath()) + textToAppend + "." +  FilenameUtils.getExtension(file.getAbsolutePath()));
    }

    public static File getFileWithNewExtension(File file, String newExtension) {
        return new File(FilenameUtils.getFullPath(file.getAbsolutePath()) + FilenameUtils.getBaseName(file.getAbsolutePath()) + "." +  newExtension);
    }


}