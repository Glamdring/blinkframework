package org.blink.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.ObserverMethod;

import org.blink.exceptions.BlinkException;
import org.blink.utils.ClassUtils;

public class ObserverMethodImpl<T> implements ObserverMethod<T> {

    private Bean<?> bean;
    private Object beanInstance;
    private AnnotatedMethod<?> method;
    private Set<Annotation> qualifiers;

    public ObserverMethodImpl(Bean<?> bean, Object beanInstance, AnnotatedMethod<?> method) {
        this.bean = bean;
        this.beanInstance = beanInstance;
        this.method = method;
        this.qualifiers = ClassUtils.getQualifiers(method.getParameters().get(0).getAnnotations());
    }

    @Override
    public Class<?> getBeanClass() {
       return bean.getBeanClass();
    }

    @Override
    public Set<Annotation> getObservedQualifiers() {
        return qualifiers;
    }

    @Override
    public Type getObservedType() {
        return ((ParameterizedType) method.getParameters().get(0).getBaseType()).getActualTypeArguments()[0];
    }

    @Override
    public Reception getReception() {
        return Reception.ALWAYS;
    }

    @Override
    public TransactionPhase getTransactionPhase() {
        return TransactionPhase.IN_PROGRESS;
    }

    @Override
    public void notify(T event) {
        try {
            method.getJavaMember().invoke(beanInstance, event);
        } catch (Exception ex) {
            throw new BlinkException(ex);
        }
    }

}
