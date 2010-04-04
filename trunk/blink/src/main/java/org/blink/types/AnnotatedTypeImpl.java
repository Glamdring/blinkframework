package org.blink.types;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

public class AnnotatedTypeImpl<T> extends AnnotatedImpl implements AnnotatedType<T> {

    private final Class<T> clazz;

    private Set<AnnotatedConstructor<T>> annotatedConstructors;
    private Set<AnnotatedField<? super T>> annotatedFields;
    private Set<AnnotatedMethod<? super T>> annotatedMethods;

    @SuppressWarnings("unchecked")
    public AnnotatedTypeImpl(Class<T> clazz) {
        super(clazz);
        this.clazz = clazz;

        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
        for (Constructor<T> constructor : constructors) {
            annotatedConstructors.add(new AnnotatedConstructorImpl<T>(this, constructor));
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            annotatedFields.add(new AnnotatedFieldImpl<T>(this, field));
        }

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            annotatedMethods.add(new AnnotatedMethodImpl<T>(this, method));
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
}
