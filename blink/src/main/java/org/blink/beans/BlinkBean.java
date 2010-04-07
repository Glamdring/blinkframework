package org.blink.beans;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

import org.blink.types.BlinkAnnotatedType;

public interface BlinkBean<T> extends Bean<T> {

    InjectionTarget<T> getInjectionTarget();

    void setInjectionTarget(InjectionTarget<T> injectionTarget);

    void initialize();

    BlinkAnnotatedType<T> getAnnotatedType();

    InjectionPoint getBeanConstructorInjectionPoint();
}
