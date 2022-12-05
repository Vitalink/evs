package org.imec.ivlab.datagenerator.uploader.service.scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.datagenerator.uploader.exception.ScannerException;

import java.io.File;
import java.util.Collection;

public class Scanner {

    private final static Logger log = LogManager.getLogger(Scanner.class);

    private static final long POLLING_INTERVAL = 2000;

    private ScannedFileHandler scannedFileHandler;
    private final File rootFolder;
    private FileAlterationMonitor monitor;

    public Scanner(File rootFolder, ScannedFileHandler scannedFileHandler) {
        this.rootFolder = rootFolder;
        this.scannedFileHandler = scannedFileHandler;
        monitor = new FileAlterationMonitor(POLLING_INTERVAL);
    }

    public void start() throws ScannerException {

        processFilesPresentAtStartup();
        startWatching();

    }

    private void processFilesPresentAtStartup() {

        File inputFolder = new File(rootFolder, "input");
        log.info("Looking for existing files at location {}", inputFolder.getAbsolutePath());
        Collection<File> filesExistingAtStartup = FileUtils.listFiles(inputFolder, getUploadFilesFilter(), TrueFileFilter.INSTANCE);

        for (File file : filesExistingAtStartup) {

            scannedFileHandler.process(rootFolder, file);

        }

    }

    private IOFileFilter getUploadFilesFilter() {

        IOFileFilter files = FileFilterUtils.fileFileFilter();
        IOFileFilter visible = HiddenFileFilter.VISIBLE;
        IOFileFilter fileFilter = FileFilterUtils.and(files, visible);

        IOFileFilter directories = FileFilterUtils.directoryFileFilter();
        IOFileFilter directoriesFilter = FileFilterUtils.and(directories, visible);

        IOFileFilter filesOrDirectories = FileFilterUtils.or(fileFilter, directoriesFilter);

        return filesOrDirectories;

    }

    private void watchRecursively(File directory) {

        log.info("Starting to watch directory: " + directory.getAbsolutePath());

        FileAlterationObserver observer = new FileAlterationObserver(directory, getUploadFilesFilter());
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {

            @Override
            public void onFileCreate(File file) {
                scannedFileHandler.process(rootFolder, file);
            }

        };

        observer.addListener(listener);

        monitor.addObserver(observer);

    }

    private void startWatching() throws ScannerException {

        watchRecursively(rootFolder);

        try {
            monitor.start();
        } catch (Exception e) {
            throw new ScannerException(e);
        }
    }


}
