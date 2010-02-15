package org.blink.beans;

import java.util.Set;

import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.BeanManager;

public interface ConfigurableBeanManager extends BeanManager {

    void addContext(Context context);

    void setClasses(Set<Class<?>> classes);

}
