package org.blink.beans;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.InvocationContext;

import org.blink.exceptions.BlinkException;

public class InterceptorBean<T> extends BeanImpl<T> implements Interceptor<T> {

    private int index;

    InterceptorBean(Class<T> clazz, ConfigurableBeanManager beanManager, int index) {
        super(clazz, beanManager);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    // implemented only for the sake of readability
    @Override
    public Set<Annotation> getInterceptorBindings() {
        return super.getInterceptorBindings();
    }

    @Override
    public Object intercept(InterceptionType interceptionType, T interceptorInstance,
            InvocationContext invocationContext) {
        AnnotatedMethod method = getMethod(interceptionType);

        try {
            return method.getJavaMember().invoke(interceptorInstance, invocationContext);
        } catch (Exception ex) {
            throw new BlinkException("Failed executing interceptor " + getBeanClass(), ex);
        }
    }

    @Override
    public boolean intercepts(InterceptionType interceptionType) {
        return getMethod(interceptionType) != null;
    }

    private AnnotatedMethod getMethod(InterceptionType interceptionType) {
        for (AnnotatedMethod method : getAnnotatedType().getMethods()) {
            if (interceptionType == InterceptionType.AROUND_INVOKE
                && method.isAnnotationPresent(AroundInvoke.class)) {
                return method;
            }

            if (interceptionType == InterceptionType.AROUND_TIMEOUT
                    && method.isAnnotationPresent(AroundTimeout.class)) {
                return method;
            }
        }

        return null;
    }
}
