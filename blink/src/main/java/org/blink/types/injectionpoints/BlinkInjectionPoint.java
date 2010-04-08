package org.blink.types.injectionpoints;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

public interface BlinkInjectionPoint<T> extends InjectionPoint {

    T getInjectableReference(BeanManager beanManager, CreationalContext<?> ctx);
}
