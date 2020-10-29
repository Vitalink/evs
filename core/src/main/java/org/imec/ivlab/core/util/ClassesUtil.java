package org.imec.ivlab.core.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClassesUtil {

    private final static Logger log = Logger.getLogger(ClassesUtil.class);

    public static <T> List<Class<T>> getClasses(String packagePath, Class<T> clazzFilter) {

        List<Class<T>> testClasses = new ArrayList<>();

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
        provider.addIncludeFilter(new AssignableTypeFilter(clazzFilter));
        Set<BeanDefinition> components = provider.findCandidateComponents(packagePath);

        for (BeanDefinition component : components) {
            try {
                Class<? extends T> cls;

                cls = Class.forName(component.getBeanClassName()).asSubclass(clazzFilter);

                if (clazzFilter.isAssignableFrom(cls)) {
                    // skip abstract classes
                    if (!Modifier.isAbstract(cls.getModifiers())) {
                        testClasses.add((Class<T>) cls);
                    }

                }

            } catch (ClassNotFoundException e) {
                log.error("error when looking for class");
            }
        }

        return testClasses;

    }

}
