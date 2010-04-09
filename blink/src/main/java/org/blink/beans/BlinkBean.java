package org.blink.beans;

import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionTarget;

import org.blink.types.BlinkAnnotatedType;
import org.blink.types.injectionpoints.BlinkInjectionPoint;
import org.blink.types.injectionpoints.ConstructorInjectionPoint;

public interface BlinkBean<T> extends Bean<T> {

    InjectionTarget<T> getInjectionTarget();

    void setInjectionTarget(InjectionTarget<T> injectionTarget);

    void initialize();

    BlinkAnnotatedType<T> getAnnotatedType();

    ConstructorInjectionPoint<T> getBeanConstructorInjectionPoint();

    ConfigurableBeanManager getBeanManager();

    Set<BlinkInjectionPoint<T>> getFieldInjectionPoints();

    Set<BlinkInjectionPoint<T>> getInitializerMethodInjectionPoints();
}