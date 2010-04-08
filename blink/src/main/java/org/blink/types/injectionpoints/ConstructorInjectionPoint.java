package org.blink.types.injectionpoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.blink.types.AnnotatedConstructorImpl;

public class ConstructorInjectionPoint<T> extends InjectionPointImpl<T> {

    public static <T> ConstructorInjectionPoint<T> create(AnnotatedConstructor<T> member,
            Bean<?> bean) {
        return new ConstructorInjectionPoint<T>(member, bean);
    }

    protected ConstructorInjectionPoint(AnnotatedConstructor<T> member, Bean<?> bean) {
        super(member, bean);
    }

    @SuppressWarnings("unchecked")
    public T newInstance(BeanManager manager, CreationalContext<?> creationalContext) {
        AnnotatedConstructorImpl<T> constructor = (AnnotatedConstructorImpl<T>) getAnnotated();
        T instance = constructor.newInstance(getParameterValues(getParameterInjectionPoints(), null, null, getBlinkBean().getBeanManager(), creationalContext));
        return instance;
    }

    @SuppressWarnings("unchecked")
    public List<BlinkInjectionPoint<T>> getParameterInjectionPoints()
    {
       final List<AnnotatedParameter<T>> params = ((AnnotatedConstructor<T>) getAnnotated()).getParameters();
       List<BlinkInjectionPoint<T>> result = new ArrayList<BlinkInjectionPoint<T>>(params.size());

       for (AnnotatedParameter<T> param : params) {
           Set<Bean<?>> beans = getBlinkBean().getBeanManager().getBeans(param.getBaseType());
           result.add(InjectionPointImpl.create(param, beans.iterator().next()));
       }

       return result;
    }

}
