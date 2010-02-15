package org.blink.beans;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

public class ContextualImpl<T> implements Contextual<T> {

    @Override
    public T create(CreationalContext<T> paramCreationalContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void destroy(T paramT, CreationalContext<T> paramCreationalContext) {
        // TODO Auto-generated method stub

    }


}
