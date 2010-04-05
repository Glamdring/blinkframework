package org.blink.beans;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
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
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Named;

import org.blink.types.AnnotatedTypeImpl;
import org.blink.types.injectionpoints.InjectionPointImpl;

public class BeanImpl<T> implements Bean<T> {

    private static final Logger logger = Logger.getLogger(BeanImpl.class.getName());

    public static final Set<Class<? extends Annotation>> SCOPES =
        new HashSet<Class<? extends Annotation>>();

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
    private AnnotatedType<T> annotatedType;
    private String name;

    public BeanImpl(Class<T> clazz) {
        beanClass = clazz;
        annotatedType = new AnnotatedTypeImpl<T>(beanClass);
        initName();
        initInjectionPoints();
        initAlternative();
    }

    private void initAlternative() {
        alternative = beanClass.isAnnotationPresent(Alternative.class);
        //TODO xml alternatives
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
        for (AnnotatedConstructor<T> constructor : annotatedType.getConstructors()) {
            injectionPoints.add(new InjectionPointImpl<T>(constructor, this));
        }
        for (AnnotatedField<? super T> field: annotatedType.getFields()) {
            injectionPoints.add(new InjectionPointImpl<T>(field, this));
        }
        for (AnnotatedMethod<? super T> method: annotatedType.getMethods()) {
            injectionPoints.add(new InjectionPointImpl<T>(method, this));
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

    @SuppressWarnings("unchecked")
    @Override
    public T create(CreationalContext<T> paramCreationalContext) {
        try {
            Constructor[] constructors = beanClass.getDeclaredConstructors();
            Constructor annotatedConstructor = null;
            for (Constructor constructor : constructors) {
                if (constructor.getAnnotation(Inject.class) != null) {
                    annotatedConstructor = constructor;
                    break;
                }
            }

            if (annotatedConstructor != null) {
                return null; //TODO
            } else {
                return beanClass.newInstance();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error creating bean", ex);
            return null;
        }
    }

    @Override
    public void destroy(T paramT, CreationalContext<T> paramCreationalContext) {
        // TODO Auto-generated method stub

    }
}
