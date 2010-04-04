package org.blink.types;

import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedMethodImpl<T> extends AnnotatedCallableImpl<T> implements AnnotatedMethod<T> {

    public AnnotatedMethodImpl(AnnotatedType<T> declaringType, Method javaMemeber) {
        super(declaringType, javaMemeber, javaMemeber.getDeclaringClass());
    }

    @Override
    public Method getJavaMember() {
        return (Method) super.getJavaMember();
    }

    @Override
    public List<AnnotatedParameter<T>> getParameters() {
        // TODO Auto-generated method stub
        return null;
    }
}
