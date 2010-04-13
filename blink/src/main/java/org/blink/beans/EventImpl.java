package org.blink.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.TypeLiteral;

import org.blink.exceptions.BlinkException;

import com.google.common.collect.Sets;

public class EventImpl<T> implements Event<T> {

    private ConfigurableBeanManager beanManager;
    private InjectionPoint injectionPoint;
    private Set<Annotation> qualifiers = Sets.newHashSet();
    private Type type;

    public EventImpl(ConfigurableBeanManager beanManager, InjectionPoint injectionPoint) {
        this(beanManager, injectionPoint, getGenericType(injectionPoint.getAnnotated()), Sets.<Annotation> newHashSet());
    }

    @SuppressWarnings("unchecked")
    private static Type getGenericType(Annotated annotated) {
        if (annotated instanceof AnnotatedField) {
            Type genericType = ((AnnotatedField) annotated).getJavaMember().getGenericType();
            if (genericType != null) {
                return genericType;
            }
        }
        if (annotated instanceof AnnotatedParameter) {
            Type type = ((AnnotatedParameter) annotated).getBaseType();
            if (type instanceof ParameterizedType) {
                return ((ParameterizedType) type).getActualTypeArguments()[0];
            }
        }
        throw new BlinkException("Only parameterized fields and params can have events injected");
    }

    public EventImpl(ConfigurableBeanManager beanManager, InjectionPoint injectionPoint, Type type, Set<Annotation> additionalQualifiers) {
        this.beanManager = beanManager;
        this.injectionPoint = injectionPoint;
        this.type = type;
        qualifiers.addAll(injectionPoint.getQualifiers());
        qualifiers.addAll(additionalQualifiers);
    }

    @Override
    public void fire(T event) {
        beanManager.fireEvent(
            event,
            getQualifiers().toArray(
                new Annotation[getQualifiers().size()]));

    }

    @Override
    public Event<T> select(Annotation... qualifiers) {
        return select(type, qualifiers);
    }

    @Override
    public <U extends T> Event<U> select(Class<U> subtype,
            Annotation... qualifiers) {
        return select((Type) subtype, qualifiers);
    }

    @Override
    public <U extends T> Event<U> select(TypeLiteral<U> subtype,
            Annotation... qualifiers) {
       return select(subtype.getType(), qualifiers);
    }

    private <U extends T> Event<U> select(Type type,
            Annotation... qualifiers) {
        return new EventImpl<U>(beanManager, injectionPoint, type, Sets.newHashSet(qualifiers));
    }
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }
}