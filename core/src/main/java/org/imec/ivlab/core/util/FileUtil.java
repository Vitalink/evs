package org.imec.ivlab.core.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class FileUtil {

    private final static Logger log = Logger.getLogger(FileUtil.class);


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
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

        FileLock lock = null;
        FileChannel channel = null;

        try { // Get a file channel for the file
            channel = new RandomAccessFile(file, "rw").getChannel();

            // Try acquiring the lock without blocking.
            // lock is null or exception if the file is already locked.
            try {
                lock = channel.tryLock();
            } catch (OverlappingFileLockException e){
                return true;
            }

        } catch (Exception e) {
            log.error("Error when checking if file is locked: ", e);
        } finally {
            if (lock != null && lock.isValid()) {
                try {
                    lock.release();
                } catch (IOException e1) {
                    log.error("Error when trying to release lock", e1);
                }
            }
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (IOException e) {
                    log.error("Error when trying to close channel", e);
                }
            }
        }

        return false;

    }

    public static File appendTextToFilename(File file, String textToAppend) {
        return new File(FilenameUtils.getFullPath(file.getAbsolutePath()) + FilenameUtils.getBaseName(file.getAbsolutePath()) + textToAppend + "." +  FilenameUtils.getExtension(file.getAbsolutePath()));
    }

    public static File getFileWithNewExtension(File file, String newExtension) {
        return new File(FilenameUtils.getFullPath(file.getAbsolutePath()) + FilenameUtils.getBaseName(file.getAbsolutePath()) + "." +  newExtension);
    }


}