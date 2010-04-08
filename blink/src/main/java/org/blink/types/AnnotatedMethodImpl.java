package org.blink.types;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedMethodImpl<T> extends AnnotatedCallableImpl<T> implements AnnotatedMethod<T> {

    public AnnotatedMethodImpl(AnnotatedType<T> declaringType, Method javaMember) {
        super(declaringType, javaMember, javaMember.getDeclaringClass());

        setAnnotatedParameters(javaMember.getGenericParameterTypes(), javaMember.getParameterAnnotations());
        setAnnotations(javaMember.getAnnotations());
    }

    @Override
    public Method getJavaMember() {
        return (Method) super.getJavaMember();
    }
}
