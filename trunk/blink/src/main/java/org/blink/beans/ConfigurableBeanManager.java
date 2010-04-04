package org.blink.beans;

import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.BeanManager;

public interface ConfigurableBeanManager extends BeanManager {

    void addContext(Context context);
}
