package org.blink.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

public class BeanImpl<T> extends ContextualImpl<T> implements Bean<T> {

    private Class<?> beanClass;
    private Set<Annotation> qualifiers;
    private Set<Class<? extends Annotation>> stereotypes;
    private Set<Type> types;
    private Set<InjectionPoint> injectionPoints;
    private boolean alternative;
    private Class<? extends Annotation> scope;
    private boolean nullable;

    protected BeanImpl(Class<?> clazz) {
        beanClass = clazz;
    }

    @Override
    public Class<?> getBeanClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Type> getTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAlternative() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isNullable() {
        // TODO Auto-generated method stub
        return false;
    }
}
