package org.blink.beans;

import java.io.Serializable;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

public class CreationalContextImpl<T> implements CreationalContext<T>, Serializable {

    private transient T incompleteInstance;
    private final transient Contextual<T> contextual;

    private transient CreationalContextImpl<?> parent;

    public CreationalContextImpl(Contextual<T> contextual) {
        this.contextual = contextual;
    }
    public CreationalContextImpl(Contextual<T> contextual, CreationalContextImpl<?> parent) {
        this.contextual = contextual;
        this.parent = parent;
    }

    public void push(T incompleteInstance) {
        this.incompleteInstance = incompleteInstance;
    }

    @SuppressWarnings("unchecked")
    public CreationalContextImpl<?> createChildContext(Contextual<?> contextual) {
        return new CreationalContextImpl(contextual, this);
    }

    @SuppressWarnings("unchecked")
    public T getIncompleteInstance(Contextual<?> contextual) {
        if (this.contextual == contextual && incompleteInstance != null) {
            return incompleteInstance;
        } else if (parent != null){
            return (T) parent.getIncompleteInstance(contextual);
        } else {
            return null;
        }
    }

    public void release() {
        incompleteInstance = null;
    }
}