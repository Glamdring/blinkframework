package org.blink.contexts;

import java.lang.annotation.Annotation;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.Contextual;

public class DependentContext extends AbstractContext {

    @Override
    protected <T> T getContextualInstance(Contextual<T> contextual) {
        return null;
    }

    @Override
    protected <T> void putContextualInstance(Contextual<T> contextual,
            T instance) {
        // No-op
    }

    @Override
    public Class<? extends Annotation> getScope() {
       return Dependent.class;
    }

}
