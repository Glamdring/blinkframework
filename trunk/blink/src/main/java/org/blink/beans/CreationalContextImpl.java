package org.blink.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

public class CreationalContextImpl<T> implements CreationalContext<T>,
        Serializable {

    private static final long serialVersionUID = 7375854583908262422L;

    // Only needed for creation, by the time it's serialized we don't need it
    private final transient Map<Contextual<?>, Object> incompleteInstances;
    private final transient Contextual<T> contextual;

//    private final DependentInstancesStore dependentInstancesStore;
//
//    private final DependentInstancesStore parentDependentInstancesStore;

    public CreationalContextImpl(Contextual<T> contextual) {
        this(contextual, new HashMap<Contextual<?>, Object>()); // new DependentInstancesStore()
    }

    private CreationalContextImpl(Contextual<T> contextual,
            Map<Contextual<?>, Object> incompleteInstances) { // DependentInstancesStore parentDependentInstancesStore
        this.incompleteInstances = incompleteInstances;
        this.contextual = contextual;

//        this.dependentInstancesStore = new DependentInstancesStore();
//        this.parentDependentInstancesStore = parentDependentInstancesStore;
    }

    public void push(T incompleteInstance) {
        incompleteInstances.put(contextual, incompleteInstance);
    }

    public <X> CreationalContextImpl<X> getCreationalContext(
            Contextual<X> contextual) {
        return new CreationalContextImpl<X>(
                contextual,
                incompleteInstances == null ? new HashMap<Contextual<?>, Object>()
                        : new HashMap<Contextual<?>, Object>(
                                incompleteInstances)); // ,dependentInstancesStore
    }

    @SuppressWarnings("unchecked")
    public <S> S getIncompleteInstance(Contextual<S> bean) {
        return incompleteInstances == null ? null : (S) incompleteInstances
                .get(bean);
    }

    public boolean containsIncompleteInstance(Contextual<?> bean) {
        return incompleteInstances == null ? false : incompleteInstances
                .containsKey(bean);
    }

    public void release() {
        // dependentInstancesStore.destroyDependentInstances();
        if (incompleteInstances != null) {
            incompleteInstances.clear();
        }
    }

    @SuppressWarnings("unchecked")
    public T getCurrentInstance() {
        return (T) incompleteInstances.values().iterator().next();
    }
}