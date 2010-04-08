package org.blink.core;

import javax.enterprise.inject.New;
import javax.enterprise.util.AnnotationLiteral;

public class NewLiteral extends AnnotationLiteral<New> implements New {

    public static final New INSTANCE = new NewLiteral();

    private NewLiteral() {
    }

    @Override
    public Class<?> value() {
        return New.class;
    }

}