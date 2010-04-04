package org.blink.types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;

import org.blink.utils.ClassUtils;

public class AnnotatedImpl implements Annotated {

    private Type baseType;
    private Set<Annotation> annotations = new HashSet<Annotation>();
    private Set<Type> typeClosures = new HashSet<Type>();

    public AnnotatedImpl(Type baseType) {
        this.baseType = baseType;
        ClassUtils.setTypeHierarchy(this.typeClosures, this.baseType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> paramClass) {
        for (Annotation annotation : this.annotations) {
            if (annotation.annotationType().equals(paramClass)) {
                return (T) annotation;
            }
        }

        return null;
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public Type getBaseType() {
        return baseType;
    }

    @Override
    public Set<Type> getTypeClosure() {
       return typeClosures;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> paramClass) {
        for (Annotation annotation : this.annotations) {
            if (annotation.annotationType().equals(paramClass)) {
                return true;
            }
        }

        return false;
    }

    protected void setAnnotations(Annotation[] annotations) {
        this.annotations.clear();
        for (Annotation annotation : annotations) {
            this.annotations.add(annotation);
        }
    }

}
