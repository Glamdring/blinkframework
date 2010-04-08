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

    private AtomicBoolean isActive = new AtomicBoolean(true);

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

        if (creationalContext == null) {
            throw new IllegalArgumentException("CreationContext cannot be null");
        }

        if (creationalContext instanceof CreationalContextImpl) {
            T incomplete = ((CreationalContextImpl<T>) creationalContext).getIncompleteInstance(contextual);
            if (incomplete != null) {
                return incomplete;
            }
        }

        instance = contextual.create(creationalContext);

        if (instance != null) {
            this.contextualInstances.put(contextual, instance);
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
