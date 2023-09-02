package org.imec.ivlab.core.kmehr.modifier.impl.util;

import be.fgov.ehealth.standards.kmehr.schema.v1.HeaderType;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.core.util.TemplateEngineUtils;

import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class HeaderUtil {

    public static HeaderType createHeader(LocalDateTime dateAndTime) throws TransformationException {

        Map<String, Object> velocityContext = new HashMap<String, Object>();
        DateTimeFormatter time = ISODateTimeFormat.time();
        DateTimeFormatter date = ISODateTimeFormat.date();

        velocityContext.put("date_today", date.print(dateAndTime));
        velocityContext.put("time_now", time.print(dateAndTime));
        String headerText = TemplateEngineUtils.generate(velocityContext, "templates/connector-outgoing-kmehrheader.xml");
        return JAXBUtils.unmarshal(HeaderType.class, headerText);

    }


}
