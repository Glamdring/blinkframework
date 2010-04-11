package org.blink.beans;

import org.blink.utils.ClassUtils;

public class BeanFactory {

    public static <T> BlinkBean<T> create(Class<T> clazz,
            ConfigurableBeanManager beanManager, int index) {
        if (ClassUtils.isDecorator(clazz)) {
            return new DecoratorBean<T>(clazz, beanManager, index);
        } else if (ClassUtils.isInterceptor(clazz)) {
        	return new InterceptorBean<T>(clazz, beanManager, index);
        } else {
            return new BeanImpl<T>(clazz, beanManager);
        }
    }

}
