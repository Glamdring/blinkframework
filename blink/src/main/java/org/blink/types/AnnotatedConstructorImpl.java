package org.blink.types;

import java.lang.reflect.Constructor;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedConstructorImpl<T> extends AnnotatedCallableImpl<T> implements AnnotatedConstructor<T> {

    public AnnotatedConstructorImpl(AnnotatedType<T> declaringType,
            Constructor javaMember) {
        super(declaringType, javaMember, javaMember.getDeclaringClass());

        setAnnotatedParameters(javaMember.getGenericParameterTypes(), javaMember.getParameterAnnotations());

    }

    @SuppressWarnings("unchecked")
    @Override
    public Constructor<T> getJavaMember() {
        return (Constructor<T>) super.getJavaMember();
    }

}
