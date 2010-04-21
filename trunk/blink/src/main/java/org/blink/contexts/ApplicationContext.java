package org.blink.contexts;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.Contextual;

public class ApplicationContext extends AbstractContext {

    private Map<Contextual<?>, Object> contextualInstances = new ConcurrentHashMap<Contextual<?>, Object>();


    @Override
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }


    @SuppressWarnings("unchecked")
    @Override
    protected <T> T getContextualInstance(Contextual<T> contextual) {
        return (T) contextualInstances.get(contextual);
    }


    @Override
    protected <T> void putContextualInstance(Contextual<T> contextual, T instance) {
        contextualInstances.put(contextual, instance);
    }


    @Override
    protected Map<Contextual<?>, Object> getContextualInstances() {
        return contextualInstances;
    }
}
