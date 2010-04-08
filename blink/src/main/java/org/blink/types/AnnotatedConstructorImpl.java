package org.blink.types;

import java.lang.reflect.Constructor;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedType;

import org.blink.exceptions.BlinkException;

public class AnnotatedConstructorImpl<T> extends AnnotatedCallableImpl<T> implements AnnotatedConstructor<T> {

    public AnnotatedConstructorImpl(AnnotatedType<T> declaringType,
            Constructor<T> javaMember) {
        super(declaringType, javaMember, javaMember.getDeclaringClass());

        setAnnotatedParameters(javaMember.getGenericParameterTypes(), javaMember.getParameterAnnotations());
        setAnnotations(javaMember.getAnnotations());

    }

    @SuppressWarnings("unchecked")
    @Override
    public Constructor<T> getJavaMember() {
        return (Constructor<T>) super.getJavaMember();
    }

    public T newInstance(Object... params) {
        try {
            return getJavaMember().newInstance(params);
        } catch (Exception ex) {
            throw new BlinkException(ex);
        }
    }

}
