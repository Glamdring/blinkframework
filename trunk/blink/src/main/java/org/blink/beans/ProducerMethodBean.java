package org.blink.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Bean;

import org.blink.exceptions.BlinkException;
import org.blink.types.AnnotatedTypeImpl;
import org.blink.types.BlinkAnnotatedType;
import org.blink.types.injectionpoints.BlinkInjectionPoint;
import org.blink.types.injectionpoints.InjectionPointImpl;
import org.blink.utils.ClassUtils;

public class ProducerMethodBean<T> extends BeanImpl<T> {

    private Method method;
    private AnnotatedMethod<? super T> annotatedMethod;
    private Bean<?> ownerBean;
    private BlinkAnnotatedType<T> annotatedOwnerType;

    public ProducerMethodBean(Bean<?> ownerBean, Method method, Class<T> clazz, ConfigurableBeanManager beanManager) {
        super(clazz, beanManager);

        this.method = method;
        method.setAccessible(true);
        this.ownerBean = ownerBean;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize() {
        annotatedOwnerType = AnnotatedTypeImpl.create((Class<T>) method.getDeclaringClass());
        for (AnnotatedMethod<? super T> am : annotatedOwnerType.getMethods()) {
            if (am.getJavaMember().equals(method)) {
                annotatedMethod = am;
                break;
            }
        }
        super.initialize();
    }

    @Override
    protected Set<Annotation> getBeanAnnotations() {
        return annotatedMethod.getAnnotations();
    }

    public Method getMethod() {
        return method;
    }

    public AnnotatedMethod<? super T> getAnnotatedMethod() {
        return annotatedMethod;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T create(CreationalContext<T> creationalContext) {
        if (ClassUtils.isStatic(method.getModifiers())) {
            try {
                return (T) method.invoke(null, (Object[]) null);
            } catch (Exception ex) {
                throw new BlinkException(ex);
            }
        } else {
            BlinkInjectionPoint<T> ip = (BlinkInjectionPoint) InjectionPointImpl.create(annotatedMethod, this);
            return (T) ip.invoke((T) ownerBean
                    .create((CreationalContext) creationalContext),
                    getBeanManager(), creationalContext);
        }

    }
}
