package org.blink.types.injectionpoints;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Set;

import javax.decorator.Delegate;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.blink.utils.ClassUtils;

public class InjectionPointImpl<T> implements InjectionPoint {

    private AnnotatedMember<? super T> member;
    private Bean<?> bean;

    public static <T> InjectionPointImpl<T> create(AnnotatedMember<? super T> member, Bean<?> bean) {
        return new InjectionPointImpl<T>(member, bean);
    }

    private InjectionPointImpl(AnnotatedMember<? super T> member, Bean<?> bean) {
        this.member = member;
        this.bean = bean;
    }

    @Override
    public Annotated getAnnotated() {
        return member;
    }

    @Override
    public Bean<?> getBean() {
        return bean;
    }

    @Override
    public Member getMember() {
        return member.getJavaMember();
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return member.getAnnotations();
    }

    @Override
    public Type getType() {
        return member.getBaseType();
    }

    @Override
    public boolean isDelegate() {
        return bean.getBeanClass().isAnnotationPresent(Delegate.class);
    }

    @Override
    public boolean isTransient() {
        return ClassUtils.isTransient(getMember().getModifiers());
    }
}
