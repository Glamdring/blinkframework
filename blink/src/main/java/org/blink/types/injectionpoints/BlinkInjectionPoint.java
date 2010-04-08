package org.blink.types.injectionpoints;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;

import org.blink.beans.ConfigurableBeanManager;

public interface BlinkInjectionPoint<T> extends InjectionPoint {

    void inject(T instance, ConfigurableBeanManager manager, CreationalContext<T> creationContext);

    void invoke(T instance, ConfigurableBeanManager manager, CreationalContext<T> creationContext);

}
