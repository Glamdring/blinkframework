package org.blink.beans;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

import org.blink.exceptions.BlinkException;
import org.blink.exceptions.DefinitionException;
import org.blink.types.BlinkAnnotatedType;

public class InjectionTargetImpl<T> implements InjectionTarget<T> {

    private BlinkAnnotatedType<T> type;
    private BeanManager beanManager;

    private List<AnnotatedMethod<? super T>> postConstructMethods;
    private List<AnnotatedMethod<? super T>> preDestroyMethods;

    public InjectionTargetImpl(AnnotatedType<T> type, BeanManager beanManager) {
        this.type = (BlinkAnnotatedType<T>) type;
        this.beanManager = beanManager;
        postConstructMethods = getMethods(PostConstruct.class);
        preDestroyMethods = getMethods(PreDestroy.class);
    }

    private List<AnnotatedMethod<? super T>> getMethods(
            Class<? extends Annotation> annotation) {
        List<AnnotatedMethod<? super T>> methods = new ArrayList<AnnotatedMethod<? super T>>();
        BlinkAnnotatedType<T> t = type;
        while (!t.getJavaClass().equals(Object.class)) {
            Set<AnnotatedMethod<? super T>> declaredMethods = t
                    .getDeclaredMethods(annotation);
            if (declaredMethods.size() > 1) {
                throw new DefinitionException("Only one " + annotation.getName()
                        + " method allowed", type);
            } else if (declaredMethods.size() == 1) {
                AnnotatedMethod<? super T> method = declaredMethods.iterator().next();
                methods.add(0, method);
            }
            t = t.getAnnotatedSuperclass();
        }
        return methods;
    }

    @Override
    public void inject(T instance, CreationalContext<T> ctx) {
        // InjectionTargetBean<T> bean = getBean(InjectionTargetBean.class);

        // bean.injectResources(instance, ctx);
        // bean.injectFields(instance, ctx);
        // bean.injectMethods(instance, ctx);
    }

    @Override
    public void postConstruct(T instance) {
        for (AnnotatedMethod<? super T> m : postConstructMethods) {
            try {
                m.getJavaMember().invoke(instance);
            } catch (Exception ex) {
                throw new BlinkException(ex);
            }
        }
    }

    @Override
    public void preDestroy(T instance) {
        for (AnnotatedMethod<? super T> m : preDestroyMethods) {
            try {
                m.getJavaMember().invoke(instance);
            } catch (Exception ex) {
                throw new BlinkException(ex);
            }
        }
    }

    @Override
    public void dispose(T paramT) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public T produce(CreationalContext<T> creationalContext) {
        // TODO Auto-generated method stub
        return null;
    }

}
