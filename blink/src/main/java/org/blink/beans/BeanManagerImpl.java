package org.blink.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.inject.Qualifier;
import javax.interceptor.InterceptorBinding;

import org.apache.commons.lang.ArrayUtils;
import org.blink.types.AnnotatedTypeImpl;
import org.blink.types.BlinkAnnotatedType;
import org.blink.utils.ClassUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class BeanManagerImpl implements ConfigurableBeanManager {

    private Set<Bean<?>> beans;
    private List<Decorator<?>> decorators = Lists.newArrayList();
    private List<Interceptor<?>> interceptors = Lists.newArrayList();

    private Map<Class<?>, AnnotatedType<?>> annotatedTypes = Maps.newHashMap();

    private Map<Contextual<?>, CreationalContext<?>> creationalContexts = Maps.newHashMap();

    public BeanManagerImpl() {

    }

    @Override
    public void initialize(Set<Bean<?>> beans) {
        this.beans = beans;

        initAnnotatedTypes();
        initDecorators();
        initInterceptors();
    }

    @SuppressWarnings("unchecked")
    private void initDecorators() {
        for (Bean<?> bean : beans) {
            if (bean instanceof Decorator) {
                decorators.add((Decorator) bean);
            }
        }

        Collections.sort(decorators, new Comparator<Decorator<?>>() {
            @Override
            public int compare(Decorator<?> d1, Decorator<?> d2) {
                return ((DecoratorBean) d1).getIndex() - ((DecoratorBean) d2).getIndex();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initInterceptors() {
        for (Bean<?> bean : beans) {
            if (bean instanceof Interceptor) {
                interceptors.add((Interceptor) bean);
            }
        }

        Collections.sort(interceptors, new InterceptorComparator());

    }

    private void initAnnotatedTypes() {
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> CreationalContext<T> createCreationalContext(
            Contextual<T> contextual) {
        //TODO consider whether caching creationalcontexts is a good idea
        CreationalContext<T> ctx = (CreationalContext<T>) creationalContexts.get(contextual);
        if (ctx == null) {
            ctx = new CreationalContextImpl<T>(contextual);
        }

        return ctx;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> InjectionTarget<T> createInjectionTarget(
            AnnotatedType<T> annotatedType) {

        //TODO ?? create another constructor of injection target?

        Set<Annotation> qualifiers = ClassUtils.getMetaAnnotations(annotatedType.getAnnotations(), Qualifier.class);
        Set<Bean<?>> beans = getBeans(annotatedType.getBaseType(), qualifiers.toArray(new Annotation[qualifiers.size()]));

        if (beans.size() == 1) {
            return new InjectionTargetImpl<T>((BlinkBean<T>) beans.iterator().next());
        } else {
            // TODO throw definition exception
            return null;
        }
    }

    @Override
    public void fireEvent(Object event,
            Annotation... qualifiers) {

        Set<ObserverMethod<? super Object>> observerMethods = resolveObserverMethods(event, qualifiers);
        for (ObserverMethod<? super Object> observerMethod : observerMethods) {
            observerMethod.notify(event);
        }

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
        return new BlinkELResolver(this);
    }

    @Override
    public Object getInjectableReference(InjectionPoint injectionPoint,
            CreationalContext<?> creationalContext) {
        return getReference(injectionPoint.getBean(), injectionPoint.getType(), creationalContext);
    }

    @Override
    public Set<Annotation> getInterceptorBindingDefinition(
            Class<? extends Annotation> clazz) {
        return ClassUtils.getMetaAnnotations(Sets.newHashSet(clazz.getAnnotations()), InterceptorBinding.class);
    }

    @Override
    public Bean<?> getPassivationCapableBean(String paramString) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getReference(Bean<?> bean, Type type,
            CreationalContext<?> creationalContext) {
        return getContext(bean.getScope()).get((Contextual) bean, creationalContext);
    }

    @Override
    public Set<Annotation> getStereotypeDefinition(
            Class<? extends Annotation> clazz) {
        return null;
    }

    @Override
    public boolean isInterceptorBinding(Class<? extends Annotation> paramClass) {
        // TODO ?
        return paramClass.isAnnotationPresent(InterceptorBinding.class);
    }

    @Override
    public boolean isNormalScope(Class<? extends Annotation> paramClass) {
        return BeanImpl.NORMAL_SCOPES.contains(paramClass);
    }

    @Override
    public boolean isPassivatingScope(Class<? extends Annotation> paramClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isQualifier(Class<? extends Annotation> paramClass) {
        return paramClass.isAnnotationPresent(Qualifier.class);
    }

    @Override
    public boolean isScope(Class<? extends Annotation> paramClass) {
        return BeanImpl.SCOPES.contains(paramClass);
    }

    @Override
    public boolean isStereotype(Class<? extends Annotation> paramClass) {
        return paramClass.isAnnotationPresent(Stereotype.class);
    }

    @Override
    public <X> Bean<? extends X> resolve(Set<Bean<? extends X>> paramSet) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Decorator<?>> resolveDecorators(Set<Type> types,
            Annotation... qualifiers) {

        List<Decorator<?>> subList = Lists.newArrayList();

        if (types == null || types.size() == 0) {
            return subList;
        }

        for (Decorator decorator : decorators) {
            if (decorator.getDelegateQualifiers().containsAll(Arrays.asList(qualifiers))
                    && decorator.getDecoratedTypes().containsAll(types)) {
                subList.add(decorator);
            }
        }

        return subList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Interceptor<?>> resolveInterceptors(
            InterceptionType interceptionType,
            Annotation... interceptorBindings) {

        List<Interceptor<?>> subList = Lists.newArrayList();
        for (Interceptor interceptor : interceptors) {
            if (interceptor.intercepts(interceptionType) &&
                    interceptor.getInterceptorBindings().containsAll(Arrays.asList(interceptorBindings))) {
                subList.add(interceptor);
            }
        }

        return subList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Set<ObserverMethod<? super T>> resolveObserverMethods(T event,
            Annotation... expectedQualifiers) {

        Set<ObserverMethod<? super T>> observerMethods = Sets.newHashSet();
        for (Bean<?> bean : beans) {
            BlinkAnnotatedType<?> annotatedType = ((BlinkBean<?>) bean).getAnnotatedType();
            for (AnnotatedMethod<?> method : annotatedType.getMethods()) {
                if (method.getParameters().size() == 1 && method.getParameters().get(0).isAnnotationPresent(Observes.class)) {
                    AnnotatedParameter param = method.getParameters().get(0);
                    Set<Annotation> paramQualifiers = param.getAnnotations();
                    if (((Class) param.getBaseType()).isAssignableFrom(event.getClass())
                            && paramQualifiers.containsAll(Sets
                                    .newHashSet(expectedQualifiers))) {
                        Object beanInstance = getReference(bean, bean.getBeanClass(),
                                createCreationalContext(bean));
                        observerMethods.add(new ObserverMethodImpl(bean, beanInstance,
                                method));
                    }
                }
            }
        }
        return observerMethods;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Bean<?> bean : beans) {
            sb.append("Bean name: "
                    + bean.getName()
                    + "; class: "
                    + bean.getBeanClass()
                    + "; types: "
                    + ArrayUtils.toString(bean.getTypes().toArray())
                    + "; qualifiers: "
                    + ArrayUtils.toString(bean.getQualifiers().toArray())
                    + "\n\n");
        }

        return sb.toString();
    }

    @Override
    public void destroy() {
        // TODO Add destruction logic
    }

}
