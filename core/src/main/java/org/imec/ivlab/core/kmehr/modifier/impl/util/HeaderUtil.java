package org.imec.ivlab.core.kmehr.modifier.impl.util;

import be.fgov.ehealth.standards.kmehr.schema.v1.HeaderType;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.core.util.TemplateEngineUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class HeaderUtil {

    public static HeaderType createHeader(LocalDateTime dateAndTime) throws TransformationException {

        Map<String, Object> velocityContext = new HashMap<String, Object>();
        velocityContext.put("date_today", dateAndTime.format(DateTimeFormatter.ISO_DATE));
        velocityContext.put("time_now", dateAndTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
        String headerText = TemplateEngineUtils.generate(velocityContext, "templates/connector-outgoing-kmehrheader.xml");
        return JAXBUtils.unmarshal(HeaderType.class, headerText);

    }


}
