package org.blink.web;

import javax.faces.context.FacesContext;

import org.blink.beans.BlinkELResolver;
import org.blink.beans.ConfigurableBeanManager;

public class BlinkWebELResolver extends BlinkELResolver {

    public BlinkWebELResolver() {
        this(null);
    }

    public BlinkWebELResolver(ConfigurableBeanManager beanManager) {
        super(beanManager);
    }

    @Override
    public ConfigurableBeanManager getBeanManager() {
        try {
            Class.forName("javax.faces.context.FacesContext");
            return new FacesBeanManagerProvider().getBeanManager();
        } catch (Throwable t) {
            return (ConfigurableBeanManager) RequestListener.currentContext
                    .get().getAttribute(StartupListener.BEAN_MANAGER_KEY);
        }

    }

    /**
     * Inner class to avoid hard-coded JSF dependency.
     */
    private static class FacesBeanManagerProvider {
        public ConfigurableBeanManager getBeanManager() {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            return (ConfigurableBeanManager) facesContext.getExternalContext()
                    .getApplicationMap().get(StartupListener.BEAN_MANAGER_KEY);
        }
    }

}
