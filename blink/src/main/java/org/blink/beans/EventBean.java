package org.blink.beans;

import java.lang.annotation.Annotation;

import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;

public class EventBean<T> extends BeanImpl<T> implements Event<T> {

    protected EventBean(Class<T> clazz, ConfigurableBeanManager beanManager) {
        super(clazz, beanManager);
    }

    @Override
    public void fire(T event) {
        getBeanManager().fireEvent(
            event,
            getQualifiers().toArray(
                new Annotation[getQualifiers().size()]));

    }

    @Override
    public Event<T> select(Annotation... qualifiers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <U extends T> Event<U> select(Class<U> subtype,
            Annotation... qualifiers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <U extends T> Event<U> select(TypeLiteral<U> subtype,
            Annotation... qualifiers) {
        // TODO Auto-generated method stub
        return null;
    }

}