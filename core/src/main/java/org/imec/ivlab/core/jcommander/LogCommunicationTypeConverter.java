package org.imec.ivlab.core.jcommander;

import com.beust.jcommander.IStringConverter;
import org.imec.ivlab.core.model.hub.Hub;
import org.imec.ivlab.core.model.hub.LogCommunicationType;


public class LogCommunicationTypeConverter implements IStringConverter<LogCommunicationType>
{
    //@Override
    public LogCommunicationType convert(String logCommunicationType) {

        return LogCommunicationType.valueOf(logCommunicationType);

    }

}