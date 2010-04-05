package org.blink.contexts;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;

public class ApplicationContextImpl extends AbstractContext {

    @Override
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }
}
