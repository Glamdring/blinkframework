package org.blink.core;

import java.util.Set;

import org.blink.beans.BeanManagerImpl;
import org.blink.beans.BeanScanner;
import org.blink.beans.ClasspathBeanScanner;
import org.blink.beans.ConfigurableBeanManager;
import org.blink.exceptions.ContextInitializationException;

public class Bootstrap {

    private BeanScanner beanScanner;
    private ConfigurableBeanManager beanManager;

    public void initialize() {
        try {
            beanScanner = new ClasspathBeanScanner();
            Set<Class<?>> classes = beanScanner.findBeans();

            beanManager = new BeanManagerImpl();
            beanManager.setClasses(classes);
        } catch (Exception ex){
            throw new ContextInitializationException(ex);
        }
    }
}
