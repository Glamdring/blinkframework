package org.blink.beans;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.InterceptorBinding;
import javax.interceptor.InvocationContext;

import org.blink.exceptions.BlinkException;
import org.blink.exceptions.DefinitionException;
import org.blink.types.AnnotatedTypeImpl;
import org.blink.types.BlinkAnnotatedType;
import org.blink.types.injectionpoints.BlinkInjectionPoint;
import org.blink.types.injectionpoints.ConstructorInjectionPoint;
import org.blink.types.injectionpoints.InjectionPointImpl;
import org.blink.utils.ClassUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class BeanImpl<T> implements BlinkBean<T> {

    private static final Logger logger = Logger.getLogger(BeanImpl.class
            .getName());

    public static final Set<Class<? extends Annotation>> SCOPES = new HashSet<Class<? extends Annotation>>();
    public static final Set<Class<? extends Annotation>> NORMAL_SCOPES = new HashSet<Class<? extends Annotation>>();
    private static final Class<? extends Annotation> DEFAULT_SCOPE = ApplicationScoped.class;
    static {
        NORMAL_SCOPES.add(ApplicationScoped.class);
        NORMAL_SCOPES.add(SessionScoped.class);
        NORMAL_SCOPES.add(RequestScoped.class);
        NORMAL_SCOPES.add(ConversationScoped.class);
        SCOPES.addAll(NORMAL_SCOPES);
        SCOPES.add(Dependent.class);
    }

    private Class<T> beanClass;
    private Set<Annotation> qualifiers = Sets.newHashSet();
    private Set<Class<? extends Annotation>> stereotypes = Sets.newHashSet();
    private Set<Type> types;
    private boolean alternative;
    private Class<? extends Annotation> scope;
    private BlinkAnnotatedType<T> annotatedType;
    private String name;

    private Set<BlinkInjectionPoint<T>> injectionPoints = Sets.newHashSet();
    private Set<BlinkInjectionPoint<T>> fieldInjectionPoints = Sets
            .newHashSet();
    private Set<BlinkInjectionPoint<T>> initializerMethodInjectionPoints = Sets
            .newHashSet();

    private InjectionTarget<T> injectionTarget;

    private ConfigurableBeanManager beanManager;

    private List<Interceptor<?>> interceptors = Lists.newArrayList();
    private Set<Annotation> interceptorBindings = Sets.newHashSet();

    private Map<Method, List<Interceptor<?>>> methodInterceptors = Maps
            .newHashMap();

    private Map<AnnotatedMethod, Set<Annotation>> methodInterceptorBindings = Maps
            .newHashMap();

    private List<Decorator<?>> decorators = Lists.newArrayList();

    protected BeanImpl(Class<T> clazz, ConfigurableBeanManager beanManager) {
        beanClass = clazz;
        this.beanManager = beanManager;
    }

    private void initAlternative() {
        alternative = beanClass.isAnnotationPresent(Alternative.class);
        // TODO xml alternatives
    }

    private void initScope() {
        for (Class<? extends Annotation> scope : SCOPES) {
            for (Annotation annotation : getBeanAnnotations()) {
                if (scope.equals(annotation.annotationType())) {
                    this.scope = scope;
                    return;
                }
            }
        }

        // if no scope annotation is present, search the stereotypes:
        for (Class<? extends Annotation> scope : SCOPES) {
            for (Class<? extends Annotation> stereotype : stereotypes) {
                if (stereotype.isAnnotationPresent(scope)) {
                    this.scope = scope;
                    return;
                }
            }
        }

        scope = DEFAULT_SCOPE;
    }

    private void initTypes() {
        if (getAnnotatedType().isAnnotationPresent(Typed.class)) {
            this.types = getTypedTypes(ClassUtils
                    .buildTypeMap(getAnnotatedType().getTypeClosure()),
                     getAnnotatedType().getAnnotation(Typed.class));
        } else {
            this.types = new HashSet<Type>(getAnnotatedType().getTypeClosure());
            if (beanClass.isInterface()) {
                this.types.add(Object.class);
            }
        }
    }

    private Set<Type> getTypedTypes(Map<Class<?>, Type> typeClosure, Typed typed) {
        Set<Type> types = new HashSet<Type>();
        for (Class<?> specifiedClass : typed.value()) {
            if (!typeClosure.containsKey(specifiedClass)) {
                throw new DefinitionException("Typed class not in hierarchy",
                        null);
            }
            types.add(typeClosure.get(specifiedClass));
        }
        types.add(Object.class);
        return types;
    }

    private void initName() {
        Named named = null;
        for (Annotation annotation : getBeanAnnotations()) {
            if (annotation.annotationType().equals(Named.class)) {
                named = (Named) annotation;
                break;
            }
        }

        // check stereotypes
        if (named == null) {
            for (Class<? extends Annotation> stereotype : stereotypes) {
                if (stereotype.isAnnotationPresent(Named.class)) {
                    named = stereotype.getAnnotation(Named.class);
                    break;
                }
            }
        }

        if (named != null) {
            name = named.value();
            if ("".equals(name) || name == null) {
                name = Introspector.decapitalize(beanClass.getSimpleName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void initInjectionPoints() {
        for (AnnotatedConstructor<T> constructor : annotatedType
                .getConstructors()) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                injectionPoints.add(InjectionPointImpl
                        .create(constructor, this));
            }
        }

        for (AnnotatedField<? super T> field : annotatedType.getFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                fieldInjectionPoints
                        .add((BlinkInjectionPoint<T>) InjectionPointImpl
                                .create(field, this));
            }
        }
        for (AnnotatedMethod<? super T> method : annotatedType.getMethods()) {
            if (method.isAnnotationPresent(Inject.class)
                    && !ClassUtils.isStatic(method.getJavaMember()
                            .getModifiers())
                    && !ClassUtils.isAbstract(method.getJavaMember()
                            .getModifiers())) {
                initializerMethodInjectionPoints
                        .add((BlinkInjectionPoint<T>) InjectionPointImpl
                                .create(method, this));
            }
        }

        injectionPoints.addAll(fieldInjectionPoints);
        injectionPoints.addAll(initializerMethodInjectionPoints);
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return (Set) injectionPoints;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return scope;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return stereotypes;
    }

    @Override
    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public boolean isAlternative() {
        return alternative;
    }

    @Override
    public boolean isNullable() {
        return !beanClass.isPrimitive();
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        T instance = getInjectionTarget().produce(creationalContext);
        getInjectionTarget().inject(instance, creationalContext);
        getInjectionTarget().postConstruct(instance);

        for (Decorator decorator : decorators) {
            Object decoratorInstance = beanManager.getReference(decorator,
                    decorator.getBeanClass(),
                    ((CreationalContextImpl<?>) creationalContext)
                            .createChildContext(decorator));
            instance = decorate(decoratorInstance, instance);
        }

        if (needsInterceptorProxy()) {
            instance = createInterceptorProxy(instance, creationalContext);
        }

        return instance;
    }

    private T createInterceptorProxy(final T instance,
            final CreationalContext cctx) {
        Class<?> originalClass = instance.getClass();

        if (instance instanceof ProxyObject) {
            originalClass = originalClass.getSuperclass();
        }

        ProxyFactory factory = new ProxyFactory();

        factory.setSuperclass(originalClass);

        factory.setHandler(new MethodHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public Object invoke(Object self, Method method, Method proceed,
                    Object[] args) throws Throwable {

                InvocationContext ctx = null;
                if (!needsMethodInterceptors()
                        || !methodInterceptors.containsKey(method)) {
                    ctx = createInvocationContext(method, args,
                            ((List) interceptors).iterator());
                } else {
                    ctx = createInvocationContext(method, args,
                            ((List) methodInterceptors.get(method)).iterator());
                }

                return ctx.proceed();
            }

            private InvocationContext createInvocationContext(Method method,
                    Object[] args, Iterator<Interceptor> chain) {
                InvocationContextImpl ctx = new InvocationContextImpl();
                ctx.setMethod(method);
                ctx.setTarget(instance);
                ctx.setParameters(args);
                ctx.setBeanManager(beanManager);
                ctx.setCreationalContext(cctx);
                // give up.. using the raw type
                ctx.setInterceptorChain(chain);

                return ctx;
            }
        });

        Class<T> proxyClass = factory.createClass();
        try {
            return proxyClass.newInstance();
        } catch (Exception ex) {
            throw new BlinkException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private T decorate(final Object decoratorInstance, final T instance) {
        Class<?> originalClass = instance.getClass();
        if (instance instanceof ProxyObject) {
            originalClass = originalClass.getSuperclass();
        }

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(originalClass);

        final Class<?> decoratorClass = decoratorInstance.getClass();
        factory.setHandler(new MethodHandler() {
            @Override
            public Object invoke(Object self, Method method, Method proceed,
                    Object[] args) throws Throwable {

                try {
                    Method decoratorMethod = decoratorClass.getMethod(method
                            .getName(), method.getParameterTypes());
                    return decoratorMethod.invoke(decoratorInstance, args);
                } catch (NoSuchMethodException ex) {
                    return method.invoke(instance, args);
                }
            }
        });

        Class<T> proxyClass = factory.createClass();
        try {
            return proxyClass.newInstance();
        } catch (Exception ex) {
            throw new BlinkException(ex);
        }
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        try {
            getInjectionTarget().preDestroy(instance);
            getInjectionTarget().dispose(instance);
            creationalContext.release();
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception in destryoing instance of "
                    + getName(), ex);
        }
    }

    @Override
    public InjectionTarget<T> getInjectionTarget() {
        return injectionTarget;
    }

    @Override
    public void setInjectionTarget(InjectionTarget<T> injectionTarget) {
        this.injectionTarget = injectionTarget;
    }

    @Override
    public BlinkAnnotatedType<T> getAnnotatedType() {
        return annotatedType;
    }

    @Override
    public void initialize() {
        annotatedType = AnnotatedTypeImpl.create(beanClass);
        initStereotypes();
        initName();
        initTypes();
        initScope();
        initQualifiers();
        initAlternative();
        initInjectionPoints();
        setInjectionTarget(new InjectionTargetImpl<T>(this));
        initInterceptorBindings();
    }

    public void initAugments() {
        initInterceptors();
        initDecorators();
    }

    @SuppressWarnings("unchecked")
    private void initInterceptorBindings() {
        interceptorBindings = ClassUtils.getMetaAnnotations(
                getBeanAnnotations(), InterceptorBinding.class);
        for (AnnotatedMethod method : getAnnotatedType().getMethods()) {
            methodInterceptorBindings.put(method, ClassUtils
                    .getMetaAnnotations(method.getAnnotations(),
                            InterceptorBinding.class));
        }
    }

    @SuppressWarnings("unchecked")
    protected void initDecorators() {
        // Should decorators be able to decorate other decorators?

        // passing no qualifiers for now. TODO
        Set classes = (Set) Sets.newHashSet(beanClass.getInterfaces());
        classes.add(beanClass);
        decorators = beanManager.resolveDecorators(classes);

        // reversing, because adding a decorator first means executing it last
        Collections.reverse(decorators);
    }

    protected void initInterceptors() {
        // Interceptors can't intercept other interceptors
        if (!ClassUtils.isInterceptor(beanClass)) {
            interceptors = getInterceptors(interceptorBindings);
            for (AnnotatedMethod method : methodInterceptorBindings.keySet()) {
                List<Interceptor<?>> tempList = getInterceptors(methodInterceptorBindings
                        .get(method));

                // adding the ones inherited from the class-level
                for (Interceptor<?> interceptor : interceptors) {
                    if (!tempList.contains(interceptor)) {
                        tempList.add(interceptor);
                    }
                }
                Collections.sort(tempList, new InterceptorComparator());
                methodInterceptors.put(method.getJavaMember(), tempList);
            }
        }
    }

    private List<Interceptor<?>> getInterceptors(
            Set<Annotation> interceptorBindings) {
        List<Interceptor<?>> interceptors = Lists.newArrayList();

        if (interceptorBindings.size() > 0) {
            for (InterceptionType type : InterceptionType.values()) {
                List<Interceptor<?>> tempList = beanManager
                        .resolveInterceptors(type, interceptorBindings
                                .toArray(new Annotation[interceptorBindings
                                        .size()]));

                interceptors.addAll(tempList);
            }

            // reversing, because adding an interceptor first means executing it
            // last
            Collections.reverse(interceptors);
        }

        return interceptors;
    }

    private void initStereotypes() {
        addStereotypes(getBeanAnnotations());
    }

    /**
     * Recursively adding stereotypes
     *
     * @param all
     *            annotations
     */
    private void addStereotypes(Set<Annotation> annotations) {
        Set<Annotation> stereotypeAnnotations = ClassUtils.getMetaAnnotations(
                annotations, Stereotype.class);

        for (Annotation stereotypeAnnotation : stereotypeAnnotations) {
            Class<? extends Annotation> stereotype = stereotypeAnnotation
                    .annotationType();

            stereotypes.add(stereotype);
            addStereotypes(Sets.newHashSet(stereotype.getAnnotations()));
        }
    }

    private void initQualifiers() {
        qualifiers = ClassUtils.getQualifiers(getBeanAnnotations());
    }

    protected Set<Annotation> getBeanAnnotations() {
        return annotatedType.getAnnotations();
    }

    public ConstructorInjectionPoint<T> getBeanConstructorInjectionPoint() {

        ConstructorInjectionPoint<T> constructorInjectionPoint = null;
        Set<AnnotatedConstructor<T>> initializerAnnotatedConstructors = annotatedType
                .getDeclaredConstructors(Inject.class);
        if (initializerAnnotatedConstructors.size() > 1) {
            if (initializerAnnotatedConstructors.size() > 1) {
                throw new DefinitionException(
                        "Multiple injectable constructors define",
                        annotatedType);
            }
        } else if (initializerAnnotatedConstructors.size() == 1) {
            constructorInjectionPoint = ConstructorInjectionPoint.create(
                    initializerAnnotatedConstructors.iterator().next(), this);
        } else if (annotatedType.getNoArgsAnnotatedConstructor() != null) {
            constructorInjectionPoint = ConstructorInjectionPoint.create(
                    annotatedType.getNoArgsAnnotatedConstructor(), this);
        }

        if (constructorInjectionPoint == null) {

            throw new DefinitionException("No suitable constructor",
                    annotatedType);
        }

        return constructorInjectionPoint;
    }

    @Override
    public ConfigurableBeanManager getBeanManager() {
        return beanManager;
    }

    @Override
    public Set<BlinkInjectionPoint<T>> getFieldInjectionPoints() {
        return fieldInjectionPoints;
    }

    @Override
    public Set<BlinkInjectionPoint<T>> getInitializerMethodInjectionPoints() {
        return initializerMethodInjectionPoints;
    }

    public List<Decorator<?>> getDecorators() {
        return decorators;
    }

    @Override
    public String toString() {
        return getName() + ":" + super.toString();
    }

    public Set<Annotation> getInterceptorBindings() {
        return interceptorBindings;
    }

    protected boolean needsInterceptorProxy() {
        if (interceptors.size() > 0) {
            return true;
        }

        for (List<Interceptor<?>> interceptors : methodInterceptors.values()) {
            if (interceptors.size() > 0) {
                return true;
            }
        }

        return false;
    }

    protected boolean needsMethodInterceptors() {
        for (List<Interceptor<?>> interceptors : methodInterceptors.values()) {
            if (interceptors.size() > 0) {
                return true;
            }
        }

        return false;
    }
}
