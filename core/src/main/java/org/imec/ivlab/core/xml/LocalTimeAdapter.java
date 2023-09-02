package org.imec.ivlab.core.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.joda.time.LocalTime;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {

    @Override
    public LocalTime unmarshal(String value) {
        // Implement deserialization logic here
        try {
            // Parse the input string to create a LocalTime object
            return LocalTime.parse(value);
        } catch (IllegalArgumentException e) {
            // Handle any parsing exceptions
            // You might want to log an error or throw an exception
            // depending on your specific use case
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String marshal(LocalTime value) {
        // Format the LocalTime as a string without the time zone indicator 'Z'
        return value.toString("HH:mm:ss");
    }
}

