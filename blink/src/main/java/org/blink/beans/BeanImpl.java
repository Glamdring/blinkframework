package org.blink.beans;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.inject.Named;

import org.blink.exceptions.DefinitionException;
import org.blink.types.AnnotatedTypeImpl;
import org.blink.types.BlinkAnnotatedType;
import org.blink.types.injectionpoints.BlinkInjectionPoint;
import org.blink.types.injectionpoints.ConstructorInjectionPoint;
import org.blink.types.injectionpoints.InjectionPointImpl;
import org.blink.utils.ReflectionUtils;

import com.google.common.collect.Sets;

public class BeanImpl<T> implements BlinkBean<T> {

    private static final Logger logger = Logger.getLogger(BeanImpl.class
            .getName());

    public static final Set<Class<? extends Annotation>> SCOPES = new HashSet<Class<? extends Annotation>>();

    private static final Class<? extends Annotation> DEFAULT_SCOPE = ApplicationScoped.class;
    static {
        SCOPES.add(ApplicationScoped.class);
        SCOPES.add(SessionScoped.class);
        SCOPES.add(RequestScoped.class);
        SCOPES.add(ConversationScoped.class);
    }

    private Class<T> beanClass;
    private Set<Annotation> qualifiers = Sets.newHashSet();
    private Set<Class<? extends Annotation>> stereotypes;
    private Set<Type> types;
    private boolean alternative;
    private Class<? extends Annotation> scope;
    private boolean nullable;
    private BlinkAnnotatedType<T> annotatedType;
    private String name;

    private Set<BlinkInjectionPoint<T>> injectionPoints;
    private Set<BlinkInjectionPoint<T>> fieldInjectionPoints;
    private Set<BlinkInjectionPoint<T>> initializerMethodInjectionPoints;


    private InjectionTarget<T> injectionTarget;

    private ConfigurableBeanManager beanManager;

    public BeanImpl(Class<T> clazz, ConfigurableBeanManager beanManager) {
        beanClass = clazz;
        this.beanManager = beanManager;
    }

    private void initAlternative() {
        alternative = beanClass.isAnnotationPresent(Alternative.class);
        // TODO xml alternatives
    }

    private void initTypes() {
        if (getAnnotatedType().isAnnotationPresent(Typed.class)) {
            this.types = getTypedTypes(ReflectionUtils.buildTypeMap(getAnnotatedType()
                    .getTypeClosure()), getAnnotatedType().getJavaClass(),
                    getAnnotatedType().getAnnotation(Typed.class));
        } else {
            this.types = new HashSet<Type>(getAnnotatedType().getTypeClosure());
            if (beanClass.isInterface()) {
                this.types.add(Object.class);
            }
        }
    }

    private Set<Type> getTypedTypes(Map<Class<?>, Type> typeClosure, Class<?> rawType,
            Typed typed) {
        Set<Type> types = new HashSet<Type>();
        for (Class<?> specifiedClass : typed.value()) {
            if (!typeClosure.containsKey(specifiedClass)) {
                throw new DefinitionException("Typed class not in hierarchy", null);
            } else {
                types.add(typeClosure.get(specifiedClass));
            }
        }
        types.add(Object.class);
        return types;
    }

    private void initName() {
        Named named = beanClass.getAnnotation(Named.class);
        if (named != null) {
            name = named.value();
            if ("".equals(name) || name == null) {
                name = Introspector.decapitalize(beanClass.getSimpleName());
            }
        }
    }

    private void initInjectionPoints() {
        Set<InjectionPoint> injectionPoints = new HashSet<InjectionPoint>();
        for (AnnotatedConstructor<T> constructor : annotatedType
                .getConstructors()) {
            injectionPoints.add(InjectionPointImpl.create(constructor, this));
        }
        for (AnnotatedField<? super T> field : annotatedType.getFields()) {
            injectionPoints.add(InjectionPointImpl.create(field, this));
        }
        for (AnnotatedMethod<? super T> method : annotatedType.getMethods()) {
            injectionPoints.add(InjectionPointImpl.create(method, this));
        }
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
        for (Class<? extends Annotation> scope : SCOPES) {
            if (annotatedType.isAnnotationPresent(scope)) {
                return scope;
            }
        }

        return DEFAULT_SCOPE;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        // TODO Auto-generated method stub
        return null;
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
        return instance;
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        try {
            getInjectionTarget().preDestroy(instance);
            getInjectionTarget().dispose(instance);
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
        initName();
        initTypes();
        initInjectionPoints();
        initAlternative();
        setInjectionTarget(new InjectionTargetImpl<T>(this));
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
            constructorInjectionPoint = ConstructorInjectionPoint.create(annotatedType
                    .getNoArgsAnnotatedConstructor(), this);
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<BlinkInjectionPoint<T>> getInitializerMethodInjectionPoints() {
        // TODO Auto-generated method stub
        return null;
    }
}
