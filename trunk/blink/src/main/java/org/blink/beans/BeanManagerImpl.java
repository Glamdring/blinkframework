package org.blink.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.ObserverMethod;

import org.blink.types.AnnotatedTypeImpl;

import com.google.common.collect.Maps;

public class BeanManagerImpl implements ConfigurableBeanManager {

    private static ConfigurableBeanManager instance;

    private Set<Bean<?>> beans;
    private Map<Class<?>, AnnotatedType<?>> annotatedTypes = Maps.newHashMap();

    private BeanManagerImpl() {

    }

    @Override
    public void initialize(Set<Bean<?>> beans) {
        this.beans = beans;

        initializeAnnotatedTypes();
    }

    public static synchronized ConfigurableBeanManager getInstance() {
        if (instance == null) {
            instance = new BeanManagerImpl();
        }

        return instance;
    }

    private void initializeAnnotatedTypes() {
        for (Bean<?> bean : beans) {
            annotatedTypes.put(bean.getBeanClass(), AnnotatedTypeImpl.create(bean.getBeanClass()));
        }
    }

    public Set<Bean<?>> getBeans() {
        return beans;
    }
    private Map<Class<? extends Annotation>, Context> contexts =
        new ConcurrentHashMap<Class<? extends Annotation>, Context>();

    @Override
    public void addContext(Context context) {
        contexts.put(context.getScope(), context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> AnnotatedType<T> createAnnotatedType(Class<T> clazz) {
        AnnotatedType<T> annotatedType = (AnnotatedType<T>) annotatedTypes.get(clazz);
        if (annotatedType == null) {
            annotatedType = AnnotatedTypeImpl.create(clazz);
        }
        return annotatedType;
    }

    @Override
    public <T> CreationalContext<T> createCreationalContext(
            Contextual<T> contextual) {
        return new CreationalContextImpl<T>(contextual);
    }

    @Override
    public <T> InjectionTarget<T> createInjectionTarget(
            AnnotatedType<T> annotatedType) {

        //TODO ?? create another constructor of injection target?
        //return new InjectionTargetImpl<T>(annotatedType);
        return null;
    }

    @Override
    public void fireEvent(Object paramObject,
            Annotation... paramArrayOfAnnotation) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<Bean<?>> getBeans(String elName) {
        Set<Bean<?>> subset = new HashSet<Bean<?>>();
        if (elName == null) {
            return subset;
        }
        for (Bean<?> bean : beans) {
            // TODO EL-Resolution
            if (elName.equals(bean.getName())) {
                subset.add(bean);
            }
        }

        return subset;
    }

    @Override
    public Set<Bean<?>> getBeans(Type type,
            Annotation... qualifiers) {
        Set<Bean<?>> subset = new HashSet<Bean<?>>();
        for (Bean<?> bean : beans) {
            if (bean.getTypes().contains(type) && bean.getQualifiers().containsAll(Arrays.asList(qualifiers))) {
                subset.add(bean);
            }
        }

        return subset;
    }

    @Override
    public Context getContext(Class<? extends Annotation> contextType) {
        Context ctx = contexts.get(contextType);
        if (ctx == null) {
            throw new IllegalStateException("No context of type " + contextType + " found");
        }

        if (!ctx.isActive()) {
           throw new IllegalStateException("Context of type " + contextType + " is not active");
        }

        return ctx;

    }

    @Override
    public ELResolver getELResolver() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getInjectableReference(InjectionPoint paramInjectionPoint,
            CreationalContext<?> paramCreationalContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Annotation> getInterceptorBindingDefinition(
            Class<? extends Annotation> paramClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bean<?> getPassivationCapableBean(String paramString) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getReference(Bean<?> paramBean, Type paramType,
            CreationalContext<?> paramCreationalContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Annotation> getStereotypeDefinition(
            Class<? extends Annotation> paramClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInterceptorBinding(Class<? extends Annotation> paramClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isNormalScope(Class<? extends Annotation> paramClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPassivatingScope(Class<? extends Annotation> paramClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isQualifier(Class<? extends Annotation> paramClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isScope(Class<? extends Annotation> paramClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isStereotype(Class<? extends Annotation> paramClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <X> Bean<? extends X> resolve(Set<Bean<? extends X>> paramSet) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Decorator<?>> resolveDecorators(Set<Type> paramSet,
            Annotation... paramArrayOfAnnotation) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Interceptor<?>> resolveInterceptors(
            InterceptionType paramInterceptionType,
            Annotation... paramArrayOfAnnotation) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> Set<ObserverMethod<? super T>> resolveObserverMethods(T paramT,
            Annotation... paramArrayOfAnnotation) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void validate(InjectionPoint paramInjectionPoint) {
        // TODO Auto-generated method stub

    }

    @Override
    public ExpressionFactory wrapExpressionFactory(
            ExpressionFactory paramExpressionFactory) {
        // TODO Auto-generated method stub
        return null;
    }
}
