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

    public ConfigurableBeanManager getBeanManager() {
        return new FacesBeanManagerProvider().getBeanManager();
    }

    /**
     * Inner class to avoid hard-coded JSF dependency.
     */
    private static class FacesBeanManagerProvider {
        public ConfigurableBeanManager getBeanManager() {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            return null;
        }
    }

}
