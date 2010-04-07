package org.blink.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.blink.beans.BeanImpl;
import org.blink.beans.BeanManagerImpl;
import org.blink.beans.BeanScanner;
import org.blink.beans.BlinkBean;
import org.blink.beans.ClasspathBeanScanner;
import org.blink.beans.ConfigurableBeanManager;
import org.blink.contexts.ApplicationContextImpl;
import org.blink.exceptions.ContextInitializationException;

public class BeanDeployer {

    private BeanScanner beanScanner;
    private ConfigurableBeanManager beanManager;

    private boolean initialized;

    @SuppressWarnings("unchecked")
    public BeanManager deployFromClassPath() {
        try {
            beanScanner = new ClasspathBeanScanner();
            Set<Class<?>> classes = beanScanner.findBeans();

            Set<Bean<?>> beans = new HashSet<Bean<?>>(classes.size());

            beanManager = BeanManagerImpl.getInstance();
            for (Class<?> clazz : classes) {
                BlinkBean<?> bean = new BeanImpl(clazz);
                bean.initialize();
                beans.add(bean);
            }
            beanManager.initialize(beans);

            return beanManager;

        } catch (Exception ex) {
            throw new ContextInitializationException(ex);
        }
    }

    public void deploy() {
        if (!initialized) {
            // TODO perform the required steps here

            // Deploy bean from XML. Also configures deployments, interceptors,
            // decorators.

            // Checking stereotype conditions

            // Configure Default Beans

            // Discover classpath classes
            deployFromClassPath();

            addContexts();
            // Check Specialization

            // Validate injection Points

            initialized = true;
        }

    }

    private void addContexts() {
        beanManager.addContext(new ApplicationContextImpl());
        // TODO more contexts
    }

    /**
     * Validates beans.
     *
     * @param beans
     *            deployed beans
     */
    private void validate(Set<Bean<?>> beans) {
        if (beans != null && beans.size() > 0) {
            for (Bean<?> bean : beans) {

                // Bean injection points
                Set<InjectionPoint> injectionPoints = bean.getInjectionPoints();

                if (injectionPoints != null) {
                    for (InjectionPoint injectionPoint : injectionPoints) {
                        if (!injectionPoint.isDelegate()) {
                            beanManager.validate(injectionPoint);
                        } else {
                            if (!bean.getBeanClass().isAnnotationPresent(
                                    javax.decorator.Decorator.class)) {
                                throw new IllegalStateException(
                                        "Delegate injection points can not defined by beans that are not decorators. Injection point : "
                                                + injectionPoint);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Discovers and deploys classes from XML.
     *
     * NOTE : Currently XML file is just used for configuring.
     *
     * @param scanner
     *            discovery scanner
     * @throws WebBeansDeploymentException
     *             if exception
     */
    protected void deployFromXML() {

        //TODO implement
        Iterator<URL> it = null;//xmlLocations.iterator();

        while (it.hasNext()) {
            URL fileURL = it.next();
            String fileName = fileURL.getFile();
            InputStream fis = null;
            try {
                fis = fileURL.openStream();

                //this.xmlConfigurator.configure(fis, fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        // all ok, ignore this!
                    }
                }
            }
        }
    }

    public BeanManager getBeanManager() {
        return beanManager;
    }
}
