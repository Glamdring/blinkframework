package org.blink.types;

import java.lang.reflect.Type;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;

public class AnnotatedParameterImpl<T> extends AnnotatedImpl implements
        AnnotatedParameter<T> {

    private AnnotatedCallable<T> declaringCallable;
    private int position;

    public AnnotatedParameterImpl(AnnotatedCallable<T> declaringCallable,
            int position, Type baseType) {
        super(baseType);
        this.declaringCallable = declaringCallable;
        this.position = position;
    }

    @Override
    public AnnotatedCallable<T> getDeclaringCallable() {
        return declaringCallable;
    }

    @Override
    public int getPosition() {
        return position;
    }
}
