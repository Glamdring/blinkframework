package org.blink.core;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.blink.beans.BeanImpl;
import org.blink.beans.BeanManagerImpl;
import org.blink.beans.BeanScanner;
import org.blink.beans.ClasspathBeanScanner;
import org.blink.beans.ConfigurableBeanManager;
import org.blink.exceptions.ContextInitializationException;

public class Bootstrap {

    private BeanScanner beanScanner;
    private ConfigurableBeanManager beanManager;

    @SuppressWarnings("unchecked")
    public BeanManager initialize() {
        try {
            beanScanner = new ClasspathBeanScanner();
            Set<Class<?>> classes = beanScanner.findBeans();

            Set<Bean<?>> beans = new HashSet<Bean<?>>(classes.size());

            for (Class<?> clazz : classes) {
                Bean<?> bean = new BeanImpl(clazz);
                beans.add(bean);
            }
            beanManager = new BeanManagerImpl(beans);

            return beanManager;

        } catch (Exception ex){
            throw new ContextInitializationException(ex);
        }
    }
}
