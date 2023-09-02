package org.imec.ivlab.validator.scanner;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.kmehr.KmehrMarshaller;
import org.imec.ivlab.core.model.upload.kmehrentrylist.BusinessData;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.validator.scanner.model.FileWithKmehrs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;


public class FolderKmehrScanner {

    private static Logger log = LogManager.getLogger(FolderKmehrScanner.class);

    public static void main(String[] args) {

        FolderKmehrScanner scanner = new FolderKmehrScanner();

        File rootFolder = new File("");
        String[] extensions = new String[] {"txt", "xml"};
        boolean recursive = true;

        ArrayList<FileWithKmehrs> kmehrMatches = scanner.scanFolders(rootFolder, extensions, recursive);
        for (FileWithKmehrs kmehrMatch : kmehrMatches) {
            log.info("Found a kmehr in file: " + kmehrMatch.getFile().getAbsolutePath());
        }

    }


    public ArrayList<FileWithKmehrs> scanFolders(File rootFolder, String[] extensions, boolean recursive) {

        ArrayList<FileWithKmehrs> kmehrMatches = new ArrayList<>();

        Collection<File> files = FileUtils.listFiles(rootFolder, extensions, recursive);

        for (File file : files) {

            log.debug("Matching file: " + file.getAbsolutePath());

            KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(file);

            if (CollectionUtils.isNotEmpty(kmehrEntryList.getKmehrEntries())) {
                log.debug("Found " + CollectionUtils.size(kmehrEntryList.getKmehrEntries()) + " kmehrs");
                for (BusinessData businessData : kmehrEntryList.getBusinessDataList()) {
                    Kmehrmessage kmehrmessage = KmehrMarshaller.fromString(businessData.getContent());
                    kmehrMatches.add(new FileWithKmehrs(file, kmehrmessage));
                }

            }

        }

        return kmehrMatches;

    }





}
