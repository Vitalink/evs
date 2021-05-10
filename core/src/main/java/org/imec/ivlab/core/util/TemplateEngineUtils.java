package org.imec.ivlab.core.util;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class TemplateEngineUtils {

    private final static Logger LOG = Logger.getLogger(TemplateEngineUtils.class);
    protected static final VelocityEngine VELOCITY_ENGINE;

    private static Map<String, Template> templateCache = new HashMap<>();

    private TemplateEngineUtils() {
        throw new UnsupportedOperationException();
    }

    public static String generate(Map<String, Object> ctx, String templateLocation) {
        VelocityContext context = new VelocityContext();
        Iterator template = ctx.entrySet().iterator();

        while(template.hasNext()) {
            Map.Entry writer = (Map.Entry)template.next();
            context.put((String)writer.getKey(), writer.getValue());
        }

        if (!templateCache.containsKey(templateLocation)) {
            templateCache.put(templateLocation, VELOCITY_ENGINE.getTemplate(templateLocation, "UTF-8"));
        }

        Template template1 = templateCache.get(templateLocation);

        StringWriter writer1 = new StringWriter();
        template1.merge(context, writer1);
        return writer1.toString();
    }

    static {
        LOG.info("Init of TemplateEngineUtils");
        VELOCITY_ENGINE = new VelocityEngine();
        VELOCITY_ENGINE.setProperty("resource.loader", "classpath");
        VELOCITY_ENGINE.setProperty("classpath.resource.loader.class", TemplateEngineUtils.ClasspathResourceLoader.class.getName());
        VELOCITY_ENGINE.setProperty("velocimacro.library", "templates/VM_connector_library.vm");
        VELOCITY_ENGINE.setProperty("resource.manager.logwhenfound", "true");
        VELOCITY_ENGINE.init();
    }


    public static class ClasspathResourceLoader extends ResourceLoader {
        public ClasspathResourceLoader() {
        }

        public void init(ExtendedProperties configuration) {
        }

        public InputStream getResourceStream(String source) throws ResourceNotFoundException {
                return IOUtils.getResourceAsStream(source);
        }

        public boolean isSourceModified(Resource resource) {
            return false;
        }

        public long getLastModified(Resource resource) {
            return (new Date()).getTime();
        }
    }
}
