package org.blink.types.injectionpoints;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.decorator.Delegate;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.blink.beans.BlinkBean;
import org.blink.beans.ConfigurableBeanManager;
import org.blink.utils.ClassUtils;

public class InjectionPointImpl<T> implements BlinkInjectionPoint<T> {

    private Annotated annotated;
    private Bean<?> bean;

    public static <T> InjectionPointImpl<T> create(AnnotatedMember<T> member,
            Bean<?> bean) {
        return new InjectionPointImpl<T>(member, bean);
    }

    public static <T> InjectionPointImpl<T> create(AnnotatedParameter<T> param,
            Bean<?> bean) {
        return new InjectionPointImpl<T>(param, bean);
    }

    protected InjectionPointImpl(Annotated member, Bean<?> bean) {
        this.annotated = member;
        this.bean = bean;
    }

    @Override
    public Annotated getAnnotated() {
        return annotated;
    }

    @Override
    public Bean<?> getBean() {
        return bean;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Member getMember() {
        if (annotated instanceof AnnotatedMember) {
            return ((AnnotatedMember<T>) annotated).getJavaMember();
        }

        return null;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return annotated.getAnnotations();
    }

    @Override
    public Type getType() {
        return annotated.getBaseType();
    }

    @Override
    public boolean isDelegate() {
        return bean.getBeanClass().isAnnotationPresent(Delegate.class);
    }

    @Override
    public boolean isTransient() {
        return ClassUtils.isTransient(getMember().getModifiers());
    }

    /**
     * Helper method for getting the current parameter values from a list of
     * annotated parameters.
     *
     * @param parameters
     *            The list of annotated parameter to look up
     * @param manager
     *            The Bean manager
     * @return The object array of looked up values
     */
    protected Object[] getParameterValues(List<BlinkInjectionPoint<T>> parameters,
            Object specialVal, Class<? extends Annotation> specialParam,
            ConfigurableBeanManager manager, CreationalContext<?> creationalContext) {
        Object[] parameterValues = new Object[parameters.size()];
        Iterator<BlinkInjectionPoint<T>> iterator = parameters.iterator();
        for (int i = 0; i < parameterValues.length; i++) {
            BlinkInjectionPoint<T> param = iterator.next();
            if (specialParam != null
                    && param.getAnnotated().isAnnotationPresent(specialParam)) {
                parameterValues[i] = specialVal;
            } else {
                parameterValues[i] = param.getInjectableReference(manager,
                        creationalContext);
            }
        }
        return parameterValues;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getInjectableReference(BeanManager beanManager, CreationalContext<?> ctx) {
        return (T) beanManager.getInjectableReference(this, ctx);
    }

    protected BlinkBean<?> getBlinkBean() {
        if (bean instanceof BlinkBean) {
            return (BlinkBean<?>) bean;
        }

        return null;
    }
}
