package org.blink.beans;

import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.blink.exceptions.ContextInitializationException;
import org.blink.utils.ClassUtils;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

public class ClasspathBeanScanner implements BeanScanner {

    private AnnotationDB annotationDb = new AnnotationDB();

    @Override
    public Set<Class<?>> findBeans() {
        try {
            URL[] beanArchives = getBeanArchives();
            annotationDb.scanArchives(beanArchives);

            Set<Class<?>> classes = new HashSet<Class<?>>();
            Map<String, Set<String>> index = this.annotationDb.getClassIndex();

            if (index != null) {
                Set<String> strSet = index.keySet();
                if (strSet != null) {
                    for (String str : strSet) {
                        Class<?> clazz = ClassUtils.getClass(str);

                        if (!clazz.isInterface() && !clazz.isAnnotation() && !ClassUtils.isAbstract(clazz.getModifiers())) {
                            classes.add(clazz);
                        }
                    }
                }
            }

            return classes;
        } catch (Exception ex) {
            throw new ContextInitializationException(ex);
        }
    }

    private URL[] getBeanArchives() {
        URL[] urls = ClasspathUrlFinder.findResourceBases("META-INF/beans.xml",
                ClassUtils.getCurrentClassLoader());

        return urls;

    }

}
