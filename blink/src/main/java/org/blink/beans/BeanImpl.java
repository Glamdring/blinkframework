package org.blink.beans;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.inject.Named;

import org.blink.exceptions.DefinitionException;
import org.blink.types.AnnotatedTypeImpl;
import org.blink.types.BlinkAnnotatedType;
import org.blink.types.injectionpoints.InjectionPointImpl;

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
    private Set<Annotation> qualifiers;
    private Set<Class<? extends Annotation>> stereotypes;
    private Set<Type> types;
    private Set<InjectionPoint> injectionPoints;
    private boolean alternative;
    private Class<? extends Annotation> scope;
    private boolean nullable;
    private BlinkAnnotatedType<T> annotatedType;
    private String name;

    private InjectionTarget<T> injectionTarget;

    public BeanImpl(Class<T> clazz) {
        beanClass = clazz;
    }

    private void initAlternative() {
        alternative = beanClass.isAnnotationPresent(Alternative.class);
        // TODO xml alternatives
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

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return injectionPoints;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return null;
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
        return null;
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
        annotatedType = new AnnotatedTypeImpl<T>(beanClass);
        initName();
        initInjectionPoints();
        initAlternative();
        setInjectionTarget(new InjectionTargetImpl<T>(this));
    }

    public InjectionPoint getBeanConstructorInjectionPoint() {

        InjectionPoint constructorInjectionPoint = null;
        Set<AnnotatedConstructor<T>> initializerAnnotatedConstructors = annotatedType
                .getDeclaredConstructors(Inject.class);
        if (initializerAnnotatedConstructors.size() > 1) {
            if (initializerAnnotatedConstructors.size() > 1) {
                throw new DefinitionException(
                        "Multiple injectable constructors define",
                        annotatedType);
            }
        } else if (initializerAnnotatedConstructors.size() == 1) {
            constructorInjectionPoint = InjectionPointImpl.create(
                    initializerAnnotatedConstructors.iterator().next(), this);
        } else if (annotatedType.getNoArgsAnnotatedConstructor() != null) {
            constructorInjectionPoint = InjectionPointImpl.create(annotatedType
                    .getNoArgsAnnotatedConstructor(), this);
        }

        if (constructorInjectionPoint == null) {
            throw new DefinitionException("No suitable constructor",
                    annotatedType);
        }

        return constructorInjectionPoint;
    }
}
