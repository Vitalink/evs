package org.imec.ivlab.ehconnector.hub.logging;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.config.EVSConfig;
import org.imec.ivlab.core.config.EVSProperties;
import org.imec.ivlab.core.model.hub.LogCommunicationType;
import org.imec.ivlab.core.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageWriter {

    private static final Logger LOG = LoggerFactory.getLogger(MessageWriter.class);

    private static LogCommunicationType getLogCommunicationType() {
        return  LogCommunicationType.fromValue(StringUtils.lowerCase(EVSConfig
            .getInstance().getProperty(EVSProperties.LOG_COMMUNICATION_TYPE)));
    }

    public static void logMessage(String message, String operation, Kind kind) {
        if (!isAllowedToLog(kind, getLogCommunicationType())) {
            return;
        }

        String rootLocation =  System.getProperty("user.dir");
        String exportDir = rootLocation + File.separator + ".." + File.separator + "logs" + File.separator + "communication";
        FileUtil.createDirectoriesRecursively(exportDir);
        LocalDateTime localDateTime = LocalDateTime.now();
        String fileName = exportDir + File.separator + localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS")) + "_" + kind.name() + "_" + operation + ".xml";

        try {
            FileUtils.writeStringToFile(new File(fileName), message, "UTF-8");
        } catch (IOException e) {
            LOG.error("Error when writing message to file" , e);
        }

    }

    private static boolean isAllowedToLog(Kind kind, LogCommunicationType logCommunicationType) {
        return logCommunicationType == LogCommunicationType.ALL ||
            (logCommunicationType == LogCommunicationType.WITHOUT_SECURITY && kind == Kind.WITHOUT_SECURITY) ||
            (logCommunicationType == LogCommunicationType.WITH_SECURITY && kind == Kind.WITH_SECURITY);
    }

}
