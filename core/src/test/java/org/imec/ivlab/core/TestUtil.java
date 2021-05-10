package org.imec.ivlab.core;

import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.util.JAXBUtils;

public class TestUtil {

    public static KmehrEntryList getKmehrEntryList (String testfilename) {

        return KmehrExtractor.getKmehrEntryList(TestResourceReader.read(testfilename));

    }

    public static <T> T filecontentToObject(Class<T> clazz, String testfilename) {
        String fileContent = TestResourceReader.read(testfilename);
        try {
            return JAXBUtils.unmarshal(clazz, fileContent);
        } catch (TransformationException e) {
            throw new RuntimeException(e);
        }
    }

}
