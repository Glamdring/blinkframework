package org.blink.types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;

import com.google.common.collect.Sets;

public final class AnnotatedTypeImpl<T> extends AnnotatedImpl implements
        BlinkAnnotatedType<T> {

    private final Class<T> clazz;

    private Set<AnnotatedConstructor<T>> annotatedConstructors = Sets
            .newHashSet();
    private Set<AnnotatedField<? super T>> annotatedFields = Sets.newHashSet();
    private Set<AnnotatedMethod<? super T>> annotatedMethods = Sets
            .newHashSet();

    private AnnotatedConstructor<T> noArgConstructor;

    public static <T> BlinkAnnotatedType<T> create(Class<T> clazz) {
        return new AnnotatedTypeImpl<T>(clazz);
    }

    @SuppressWarnings("unchecked")
    private AnnotatedTypeImpl(Class<T> clazz) {
        super(clazz);
        if (clazz == null) {
            throw new IllegalArgumentException("Passed Class must not be null");
        }
        this.clazz = clazz;

        Constructor<T>[] constructors = (Constructor<T>[]) clazz
                .getDeclaredConstructors();
        for (Constructor<T> constructor : constructors) {
            if (constructor.getAnnotations().length > 0
                    || hasParameterAnnotations(constructor.getParameterAnnotations())) {
                annotatedConstructors.add(new AnnotatedConstructorImpl<T>(this,
                        constructor));
            }
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotations().length > 0) {
                annotatedFields.add(new AnnotatedFieldImpl<T>(this, field));
            }
        }

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getAnnotations().length > 0
                    || hasParameterAnnotations(method.getParameterAnnotations())) {
                annotatedMethods.add(new AnnotatedMethodImpl<T>(this, method));
            }
        }

        setAnnotations(clazz.getAnnotations());

        initNoArgContructor();
    }

    private boolean hasParameterAnnotations(Annotation[][] parameterAnnotations) {
        for (Annotation[] annotations : parameterAnnotations) {
            if (annotations.length > 0) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void initNoArgContructor() {
        for (Constructor<?> constructor : getJavaClass().getConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
                noArgConstructor = new AnnotatedConstructorImpl<T>(this,
                        (Constructor<T>) constructor);
            }
        }
    }

    @Override
    public Set<AnnotatedConstructor<T>> getConstructors() {
        return annotatedConstructors;
    }

    @Override
    public Set<AnnotatedField<? super T>> getFields() {
        return annotatedFields;
    }

    @Override
    public Class<T> getJavaClass() {
        return clazz;
    }

    @Override
    public Set<AnnotatedMethod<? super T>> getMethods() {
        return annotatedMethods;
    }

    @Override
    public Set<AnnotatedMethod<? super T>> getDeclaredMethods(
            Class<? extends Annotation> annotation) {
        Set<AnnotatedMethod<? super T>> result = new HashSet<AnnotatedMethod<? super T>>();
        for (AnnotatedMethod<? super T> m : annotatedMethods) {
            if (m.isAnnotationPresent(annotation)) {
                result.add(m);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlinkAnnotatedType<T> getAnnotatedSuperclass() {
        // TODO load from a cache
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            return (BlinkAnnotatedType<T>) AnnotatedTypeImpl.create(superclass);
        }

        return null;
    }

    @Override
    public Set<AnnotatedConstructor<T>> getDeclaredConstructors(
            Class<? extends Annotation> annotation) {
        Set<AnnotatedConstructor<T>> result = new HashSet<AnnotatedConstructor<T>>();
        for (AnnotatedConstructor<T> m : annotatedConstructors) {
            if (m.isAnnotationPresent(annotation)) {
                result.add(m);
            }
        }
        return result;
    }

    @Override
    public AnnotatedConstructor<T> getNoArgsAnnotatedConstructor() {
        return noArgConstructor;
    }
}
