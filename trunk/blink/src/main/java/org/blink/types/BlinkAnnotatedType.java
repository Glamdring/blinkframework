package org.blink.types;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

public interface BlinkAnnotatedType<T> extends AnnotatedType<T> {

    Set<AnnotatedMethod<? super T>> getDeclaredMethods(Class<? extends Annotation> annotation);

    Set<AnnotatedConstructor<T>> getDeclaredConstructors(Class<? extends Annotation> annotation);

    BlinkAnnotatedType<T> getAnnotatedSuperclass();

    /**
     * No no-arg constructor may not be annotated, but is still returned as AnnotatedConstructor
     * That is in order to fit in the general mechanism for creating beans
     * @return no args constructor
     */
    AnnotatedConstructor<T> getNoArgsAnnotatedConstructor();
}
