package org.blink.contexts;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.Contextual;

/**
 * The request context is based on a ThreadLocal, because each request is handled in exactly one thread
 * @author Bozhidar Bozhanov
 *
 */
public class RequestContext extends AbstractContext {

    private ThreadLocal<Map<Contextual<?>, Object>> contextualInstances;

    {
        contextualInstances = new ThreadLocal<Map<Contextual<?>, Object>>();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return RequestScoped.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T getContextualInstance(Contextual<T> contextual) {
        return (T) getContextualInstances().get(contextual);
    }

    @Override
    protected <T> void putContextualInstance(Contextual<T> contextual, T instance) {
       getContextualInstances().put(contextual, instance);
    }

    @Override
    protected Map<Contextual<?>, Object> getContextualInstances() {
        Map<Contextual<?>, Object> map = contextualInstances.get();
        if (map == null) {
            map = new ConcurrentHashMap<Contextual<?>, Object>();
            contextualInstances.set(map);
        }

        return map;
    }
}
