package org.blink.beans;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.CreationException;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

import org.blink.exceptions.BlinkException;
import org.blink.exceptions.DefinitionException;
import org.blink.types.BlinkAnnotatedType;
import org.blink.types.injectionpoints.BlinkInjectionPoint;
import org.blink.types.injectionpoints.ConstructorInjectionPoint;

public class InjectionTargetImpl<T> implements InjectionTarget<T> {

    private BlinkAnnotatedType<T> type;
    private BlinkBean<T> bean;

    private List<AnnotatedMethod<? super T>> postConstructMethods;
    private List<AnnotatedMethod<? super T>> preDestroyMethods;

    public InjectionTargetImpl(BlinkBean<T> bean) {
        this.bean = bean;
        this.type = bean.getAnnotatedType();
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

        injectFieldsAndInitializers(instance, ctx, bean.getBeanManager(), bean
                .getFieldInjectionPoints(), bean.getInitializerMethodInjectionPoints());
        // TODO
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
    public void dispose(T instance) {
        // No-op TODO ?
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return bean.getInjectionPoints();
    }

    @Override
    public T produce(CreationalContext<T> creationalContext) {
        if (creationalContext == null) {
            throw new IllegalArgumentException("The CreationalContext may not be null");
        }
        ConstructorInjectionPoint<T> constructorInjectionPoint = bean
                .getBeanConstructorInjectionPoint();
        T instance = constructorInjectionPoint.newInstance(bean.getBeanManager(),
                creationalContext);
        creationalContext.push(instance);
        return instance;
    }

    protected void injectFieldsAndInitializers(T instance, CreationalContext<T> ctx,
            BeanManagerImpl beanManager,
            List<? extends Iterable<? extends BlinkInjectionPoint<?>>> injectableFields,
            List<? extends Iterable<? extends BlinkInjectionPoint<?>>> initializerMethods) {

        for (int i = 0; i < injectableFields.size(); i++) {
            injectBoundFields(instance, ctx, beanManager, injectableFields.get(i));
        }

        for (int i = 0; i < initializerMethods.size(); i++) {
            callInitializers(instance, ctx, beanManager, initializerMethods.get(i));
        }
    }

    protected void injectBoundFields(T instance, CreationalContext<T> creationalContext,
            ConfigurableBeanManager manager,
            Iterable<? extends BlinkInjectionPoint<?>> injectableFields) {

        for (BlinkInjectionPoint<?> injectableField : injectableFields) {
            injectableField.inject(instance, manager, creationalContext);
        }
    }

    public static <T> void callInitializers(T instance,
            CreationalContext<T> creationalContext, ConfigurableBeanManager manager,
            Iterable<? extends BlinkInjectionPoint<?>> initializerMethods) {
        for (BlinkInjectionPoint<?> initializer : initializerMethods) {
            initializer.invoke(instance, manager, creationalContext,
                    CreationException.class);
        }
    }
}
