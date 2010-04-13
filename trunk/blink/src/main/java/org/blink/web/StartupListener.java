package org.blink.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.blink.beans.BeanManagerImpl;
import org.blink.beans.ConfigurableBeanManager;
import org.blink.core.BeanDeployer;

public class StartupListener implements ServletContextListener {

    public static final String BEAN_MANAGER_KEY = BeanManagerImpl.class.toString();

    @Override
    public void contextDestroyed(ServletContextEvent evt) {
        ((ConfigurableBeanManager) evt.getServletContext().getAttribute(BEAN_MANAGER_KEY)).destroy();
    }

    @Override
    public void contextInitialized(ServletContextEvent evt) {
        BeanDeployer deployer = new BeanDeployer();
        deployer.deploy();
        evt.getServletContext().setAttribute(BEAN_MANAGER_KEY, deployer.getBeanManager());
    }

}
