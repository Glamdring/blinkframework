package org.blink.types;

import java.lang.reflect.Member;
import java.lang.reflect.Type;

import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedType;

import org.blink.utils.ClassUtils;

public class AnnotatedMemberImpl<T> extends AnnotatedImpl implements AnnotatedMember<T> {

    private AnnotatedType<T> declaringType;
    private Member javaMember;

    public AnnotatedMemberImpl(AnnotatedType<T> declaringType, Member javaMemeber, Type baseType) {
        super(baseType);
        this.declaringType = declaringType;
        this.javaMember = javaMemeber;
    }
    @Override
    public AnnotatedType<T> getDeclaringType() {
        return declaringType;
    }

    @Override
    public Member getJavaMember() {
        return javaMember;
    }

    @Override
    public boolean isStatic() {
        return ClassUtils.isStatic(getJavaMember().getModifiers());
    }

}
