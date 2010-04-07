package org.blink.types;

import java.lang.reflect.Field;

import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedFieldImpl<T> extends AnnotatedMemberImpl<T> implements AnnotatedField<T> {

    public AnnotatedFieldImpl(AnnotatedType<T> declaringType, Field javaMember) {
        super(declaringType, javaMember, javaMember.getDeclaringClass());
        setAnnotations(javaMember.getAnnotations());
    }

    @Override
    public Field getJavaMember() {
        return (Field) super.getJavaMember();
    }

}
