package org.blink.contexts;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.blink.beans.CreationalContextImpl;

public abstract class AbstractContext implements Context {

    private AtomicBoolean isActive;

    private Map<Contextual<?>, Object> contextualInstances = new ConcurrentHashMap<Contextual<?>, Object>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Contextual<T> contextual) {
        checkActive();

        return (T) contextualInstances.get(contextual);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Contextual<T> contextual,
            CreationalContext<T> creationalContext) {
        checkActive();

        T instance = (T) contextualInstances.get(contextual);

        if (instance != null) {
            return instance;
        }

        //TODO rethink this
        if (creationalContext == null) {
            return null;
        }
        // Check for incomplete instance, putting for circular references
        if (creationalContext instanceof CreationalContextImpl) {
            CreationalContextImpl<?> cctx = (CreationalContextImpl<?>) creationalContext;
            if (cctx.getCurrentInstance() != null) {
                instance = (T) cctx.getCurrentInstance();
            }
        }

        if (instance == null) {
            instance = contextual.create(creationalContext);
        }

        if (instance != null) {
            this.contextualInstances.put(contextual, instance);
            //this.contextualInstances.put(contextual, creationalContext);
        }

        return instance;
    }

    @Override
    public boolean isActive() {
        return isActive.get();
    }

    private void checkActive() {
        if (!isActive()) {
            throw new ContextNotActiveException("Context of scope "
                    + getScope().getName() + " is not active");
        }
    }
}
