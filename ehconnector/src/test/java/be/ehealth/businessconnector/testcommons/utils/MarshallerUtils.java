package be.ehealth.businessconnector.testcommons.utils;

import be.ehealth.technicalconnector.utils.ConnectorIOUtils;
import be.ehealth.technicalconnector.utils.ConnectorXmlUtils;

import java.io.InputStream;

/**
 * @author Olivier Chapuis (eh077)
 */
public class MarshallerUtils {

    /**
     * Generic method to unmarshall a xml resource.
     *
     * @param location the resource location.
     * @param clazz the class type to unmarshal to.
     * @param <T> the type parameter.
     * @return the object representation of the resource.
     * @throws Exception
     */
    public static <T> T toObject(String location, Class<T> clazz) throws Exception {
        InputStream is = ConnectorIOUtils.getResourceAsStream(location);
        return ConnectorXmlUtils.toObject(is, clazz);
    }
}
