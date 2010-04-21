package org.blink.beans;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.decorator.Decorator;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.blink.exceptions.BlinkException;
import org.blink.exceptions.ContextInitializationException;
import org.blink.exceptions.DefinitionException;
import org.blink.utils.ClassUtils;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import com.google.common.collect.Sets;

public class ClasspathBeanScanner implements BeanScanner {

    private AnnotationDB annotationDb = new AnnotationDB();

    @Override
    public Set<BeanClassDescriptor> findBeans() {
        try {
            URL[] beanArchives = getBeanArchives();
            annotationDb.scanArchives(beanArchives);

            Set<BeanClassDescriptor> classes = Sets.newHashSet();
            Map<String, Set<String>> index = this.annotationDb.getClassIndex();

            if (index != null) {
                Set<String> strSet = index.keySet();
                if (strSet != null) {
                    for (String str : strSet) {
                        Class<?> clazz = ClassUtils.getClass(str);

                        int decoratorIndex = getComponentIndex(clazz, "decorators");
                        int interceptorIndex = getComponentIndex(clazz, "interceptors");

                        boolean isDecorator = ClassUtils.isDecorator(clazz);
                        boolean isInterceptor = ClassUtils.isInterceptor(clazz);

                        // See 3.1.1 of the JSR-299 spec
                        if (!clazz.isInterface()
                                && !clazz.isAnnotation()
                                && !(ClassUtils.isInnerClass(clazz) && ClassUtils
                                        .isStatic(clazz.getModifiers()))
                                && (!ClassUtils
                                        .isAbstract(clazz.getModifiers()) || clazz
                                        .isAnnotationPresent(Decorator.class))
                                && !Extension.class.isAssignableFrom(clazz)
                                && hasAppropriateConstructor(clazz)
                                && isDecorator ? decoratorIndex != -1 : true
                                && isInterceptor ? interceptorIndex != -1 : true) {

                            if (isDecorator) {
                                classes.add(new BeanClassDescriptor(clazz, decoratorIndex));
                            } else if (isInterceptor) {
                                classes.add(new BeanClassDescriptor(clazz, interceptorIndex));
                            } else {
                                classes.add(new BeanClassDescriptor(clazz));
                            }
                        }
                    }
                }
            }

            return classes;
        } catch (Exception ex) {
            throw new ContextInitializationException(ex);
        }
    }

    private int getComponentIndex(Class<?> clazz, String tagName) {
        try {
            InputStream is = clazz.getResourceAsStream("/META-INF/beans.xml");
            XMLInputFactory f = XMLInputFactory.newInstance();
            XMLStreamReader r = f.createXMLStreamReader(is);
            boolean parsingDecorators = false;
            try {
                int idx = 0;
                while (r.hasNext()) {
                    int eventCode = r.next();
                    if (eventCode == XMLStreamReader.START_ELEMENT
                            && r.getName().getLocalPart().equals(tagName)) {

                        parsingDecorators = true;
                    }
                    if (eventCode == XMLStreamReader.END_ELEMENT
                            && r.getName().getLocalPart().equals(tagName)) {

                        break;
                    }

                    if (eventCode == XMLStreamReader.START_ELEMENT
                            && r.getName().getLocalPart().equals("class")
                            && parsingDecorators) {

                        String className = r.getElementText();
                        try {
                            Class.forName(className);
                        } catch (ClassNotFoundException ex) {
                            throw new DefinitionException("Class " + className + " defined in " + tagName + " does not exist");
                        }
                        if (clazz.getName().equals(className)) {
                            return idx;
                        }
                        idx++;
                    }
                }
            } finally {
                r.close();
                is.close();
            }
            return -1;
        } catch (Exception ex) {
            throw new BlinkException(
                    "Malformed beans.xml in bean archive of class "
                            + clazz.getName(), ex);
        }
    }

    private boolean hasAppropriateConstructor(Class<?> clazz) {
        for (Constructor c : clazz.getDeclaredConstructors()) {
            if (c.getParameterTypes().length == 0
                    || c.isAnnotationPresent(Inject.class)) {
                return true;
            }
        }

        return false;
    }

    private URL[] getBeanArchives() {
        URL[] urls = ClasspathUrlFinder.findResourceBases("META-INF/beans.xml",
                ClassUtils.getCurrentClassLoader());

        return urls;

    }

}
