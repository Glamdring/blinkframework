package org.blink.beans;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.interceptor.InvocationContext;

import com.google.common.collect.Maps;

public class InvocationContextImpl implements InvocationContext {

    private Method method;
    private Object[] parameters;
    private Object target;
    private Object timer;
    private Map<String, Object> contextData = Maps.newHashMap();
    private Iterator<Interceptor> interceptorChain;
    private ConfigurableBeanManager beanManager;
    private CreationalContext creationalContext;

    public InvocationContextImpl(){

    }

    @Override
    public Map<String, Object> getContextData() {
        // not implemented
        return contextData;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Object getTimer() {
        return timer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object proceed() throws Exception {
        if (interceptorChain.hasNext()) {
            Interceptor interceptor = interceptorChain.next();
            Object interceptorInstance = beanManager.getReference(interceptor, interceptor.getBeanClass(), creationalContext);
            return interceptor.intercept(
                    InterceptionType.AROUND_INVOKE, interceptorInstance, this);
        } else {
            return method.invoke(target, parameters);
        }
    }

    @Override
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;
    }

    public void setTimer(Object timer) {
        this.timer = timer;
    }

    public Iterator<Interceptor> getInterceptorChain() {
        return interceptorChain;
    }

    public void setInterceptorChain(Iterator<Interceptor> interceptorChain) {
        this.interceptorChain = interceptorChain;
    }

    public void setBeanManager(ConfigurableBeanManager beanManager) {
        this.beanManager = beanManager;
    }

    public void setCreationalContext(CreationalContext creationalContext) {
        this.creationalContext = creationalContext;
    }
}
