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
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.apache.commons.lang.ArrayUtils;
import org.blink.core.AnyLiteral;
import org.blink.core.DefaultLiteral;
import org.blink.core.NewLiteral;
import org.blink.exceptions.DefinitionException;
import org.blink.types.AnnotatedImpl;
import org.blink.types.AnnotatedTypeImpl;
import org.blink.types.BlinkAnnotatedType;
import org.blink.types.injectionpoints.BlinkInjectionPoint;
import org.blink.types.injectionpoints.ConstructorInjectionPoint;
import org.blink.types.injectionpoints.InjectionPointImpl;
import org.blink.utils.ClassUtils;
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
    private Set<Class<? extends Annotation>> stereotypes = Sets.newHashSet();
    private Set<Type> types;
    private boolean alternative;
    private Class<? extends Annotation> scope;
    private BlinkAnnotatedType<T> annotatedType;
    private String name;

    private Set<BlinkInjectionPoint<T>> injectionPoints = Sets.newHashSet();
    private Set<BlinkInjectionPoint<T>> fieldInjectionPoints = Sets.newHashSet();
    private Set<BlinkInjectionPoint<T>> initializerMethodInjectionPoints = Sets.newHashSet();


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
            }
            types.add(typeClosure.get(specifiedClass));
        }
        types.add(Object.class);
        return types;
    }

    private void initName() {
        Named named = beanClass.getAnnotation(Named.class);

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
                injectionPoints.add(InjectionPointImpl.create(constructor, this));
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
                    && !ClassUtils.isStatic(method.getJavaMember().getModifiers())
                    && !ClassUtils.isAbstract(method.getJavaMember().getModifiers())) {
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
        initStereotypes();
        initName();
        initTypes();
        initScope();
        initQualifiers();
        initAlternative();
        initInjectionPoints();
        setInjectionTarget(new InjectionTargetImpl<T>(this));
    }

    private void initStereotypes() {
        addStereotypes(getBeanAnnotations());
    }

    /**
     * Recursively adding stereotypes
     * @param all annotations
     */
    private void addStereotypes(Set<Annotation> annotations) {
        Set<Annotation> stereotypeAnnotations = AnnotatedImpl.getMetaAnnotations(annotations, Stereotype.class);

        for (Annotation stereotypeAnnotation : stereotypeAnnotations) {
            Class<? extends Annotation> stereotype = stereotypeAnnotation.annotationType();

            stereotypes.add(stereotype);
            addStereotypes(Sets.newHashSet(stereotype.getAnnotations()));
        }
    }

    private void initQualifiers() {
        for (Annotation annotation: getBeanAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                qualifiers.add(annotation);
            }
        }

        if (qualifiers.size() == 0 || (qualifiers.size() == 1 && qualifiers.iterator().next().annotationType() == Named.class)) {
            qualifiers.add(DefaultLiteral.INSTANCE);
        }

        if (!qualifiers.contains(NewLiteral.INSTANCE)) {
            qualifiers.add(AnyLiteral.INSTANCE);
        }
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
        return fieldInjectionPoints;
    }

    @Override
    public Set<BlinkInjectionPoint<T>> getInitializerMethodInjectionPoints() {
        return initializerMethodInjectionPoints;
    }
}
