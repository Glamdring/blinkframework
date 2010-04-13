package org.blink.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.TypeLiteral;

import org.blink.core.AnyLiteral;
import org.blink.exceptions.BlinkException;

import com.google.common.collect.Sets;

public class InstanceImpl<T> implements Instance<T> {

    private ConfigurableBeanManager beanManager;
    private InjectionPoint injectionPoint;
    private Set<Bean<?>> beans;
    private Set<Annotation> qualifiers = Sets.newHashSet();
    private Type type;

    public InstanceImpl(ConfigurableBeanManager beanManager, InjectionPoint injectionPoint) {
        this(beanManager, injectionPoint, injectionPoint.getType(), Sets.<Annotation>newHashSet());
    }

    public InstanceImpl(ConfigurableBeanManager beanManager, InjectionPoint injectionPoint, Type type, Set<Annotation> additionalQualifiers) {
        this.beanManager = beanManager;
        this.injectionPoint = injectionPoint;
        this.type = type;
        qualifiers.addAll(injectionPoint.getQualifiers());
        qualifiers.addAll(additionalQualifiers);
    }

    @Override
    public boolean isAmbiguous() {
        return getBeans().size() > 1;
    }
    @Override
    public boolean isUnsatisfied() {
        return getBeans().size() == 0;
    }

    @Override
    public Instance<T> select(Annotation... qualifiers) {
        return select(type, qualifiers);
    }
    @Override
    public <U extends T> Instance<U> select(Class<U> subclass,
            Annotation... qualifiers) {
        return select((Type) subclass, qualifiers);
    }

    @Override
    public <U extends T> Instance<U> select(TypeLiteral<U> typeLiteral,
            Annotation... qualifiers) {
        return select(typeLiteral.getType(), qualifiers);
    }

    private <U extends T> Instance<U> select(Type type,
            Annotation... qualifiers) {
        if (this.qualifiers.contains(AnyLiteral.INSTANCE)) {
            return new InstanceImpl<U>(beanManager, injectionPoint, type, Sets.newHashSet(qualifiers));
        } else {
            throw new BlinkException("Cannot dynamically select the instnace type unless it is annotated with @Any");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<T> iterator() {
        Collection<T> instances = new ArrayList<T>();
        for (Bean<?> bean : getBeans()) {
            Object object = beanManager.getReference(bean, injectionPoint.getType(),
                    beanManager.createCreationalContext(bean));

            T instance = (T) object;

            instances.add(instance);
        }
        return instances.iterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        if (isAmbiguous()) {
            throw new BlinkException("Instance is still ambigous");
        }
        if (isUnsatisfied()) {
            throw new BlinkException("Instance is not satisfied");
        }

       Bean<?> bean = getBeans().iterator().next();

       return (T) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));
    }

    private Set<Bean<?>> getBeans() {
        if (beans == null) {
            beans = beanManager.getBeans(type, qualifiers.toArray(
                        new Annotation[qualifiers.size()]));
        }

        return beans;
    }

}
