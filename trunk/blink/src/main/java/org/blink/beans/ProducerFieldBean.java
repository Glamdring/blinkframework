package org.blink.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.Bean;

import org.blink.exceptions.BlinkException;
import org.blink.types.AnnotatedTypeImpl;
import org.blink.types.BlinkAnnotatedType;
import org.blink.utils.ClassUtils;

public class ProducerFieldBean<T> extends BeanImpl<T> {

    private Field field;
    private AnnotatedField<? super T> annotatedField;
    private Bean<?> ownerBean;
    private BlinkAnnotatedType<T> annotatedOwnerType;

    public ProducerFieldBean(Bean<?> ownerBean, Field field, Class<T> clazz,
            ConfigurableBeanManager beanManager) {
        super(clazz, beanManager);

        this.field = field;
        field.setAccessible(true);
        this.ownerBean = ownerBean;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize() {
        annotatedOwnerType = AnnotatedTypeImpl.create((Class<T>) field
                .getDeclaringClass());
        for (AnnotatedField<? super T> af : annotatedOwnerType.getFields()) {
            if (af.getJavaMember().equals(field)) {
                annotatedField = af;
                break;
            }
        }
        super.initialize();
    }

    @Override
    protected Set<Annotation> getBeanAnnotations() {
        return annotatedField.getAnnotations();
    }

    public Field getField() {
        return field;
    }

    public AnnotatedField<? super T> getAnnotatedField() {
        return annotatedField;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T create(CreationalContext<T> creationalContext) {
        try {
            if (ClassUtils.isStatic(field.getModifiers())) {
                return (T) field.get(null);
            } else {
                return (T) field.get(ownerBean
                        .create((CreationalContext) creationalContext));
            }
        } catch (Exception ex) {
            throw new BlinkException(ex);
        }

    }
}
