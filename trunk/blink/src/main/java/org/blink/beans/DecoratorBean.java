package org.blink.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.decorator.Delegate;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.InjectionPoint;

import org.blink.exceptions.DefinitionException;
import org.blink.types.injectionpoints.BlinkInjectionPoint;

import com.google.common.collect.Sets;

public class DecoratorBean<T> extends BeanImpl<T> implements Decorator<T> {

    private BlinkInjectionPoint delegateInjectionPoint;
    private Annotated annotatedDelegate;
    private Set<Annotation> delegateQualifiers;
    private int index;
    private Set<Type> decoratedTypes;

    DecoratorBean(Class<T> clazz, ConfigurableBeanManager beanManager, int index) {
        super(clazz, beanManager);
        this.index = index;
    }

    @Override
    public void initialize() {
        super.initialize();

        for (InjectionPoint ip : getInjectionTarget().getInjectionPoints()) {
            Annotated annotated = ip.getAnnotated();
            if (annotated instanceof AnnotatedCallable) {
                AnnotatedCallable<?> callable = (AnnotatedCallable<?>) annotated;
                for (AnnotatedParameter param : callable.getParameters()) {
                    if (param.isAnnotationPresent(Delegate.class)) {
                        delegateInjectionPoint = (BlinkInjectionPoint) ip;
                        annotatedDelegate = param;
                    }
                }
            } else {
                if (annotated.isAnnotationPresent(Delegate.class)) {
                    delegateInjectionPoint = (BlinkInjectionPoint) ip;
                    annotatedDelegate = annotated;
                }
            }
        }

        if (delegateInjectionPoint == null) {
            throw new DefinitionException("No delegate injection point for bean " + getBeanClass());
        }
        initDelegateQualifiers();
        initDecoratedTypes();
    }

    @SuppressWarnings("unchecked")
    private void initDecoratedTypes() {
        decoratedTypes = (Set) Sets.newHashSet(getBeanClass().getInterfaces());
        decoratedTypes.addAll(annotatedDelegate.getTypeClosure());
    }

    private void initDelegateQualifiers() {
        delegateQualifiers = getQualifiers(annotatedDelegate.getAnnotations());
    }

    @Override
    public Set<Type> getDecoratedTypes() {
        return decoratedTypes;
    }

    @Override
    public Set<Annotation> getDelegateQualifiers() {
        return delegateQualifiers;
    }

    @Override
    public Type getDelegateType() {
        return annotatedDelegate.getBaseType();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setDelegateQualifiers(Set<Annotation> delegateQualifiers) {
        this.delegateQualifiers = delegateQualifiers;
    }
}
