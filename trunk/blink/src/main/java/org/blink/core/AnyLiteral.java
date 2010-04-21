package org.blink.core;

import javax.enterprise.inject.Any;
import javax.enterprise.util.AnnotationLiteral;

public final class AnyLiteral extends AnnotationLiteral<Any> implements Any {

    public static final Any INSTANCE = new AnyLiteral();

    private AnyLiteral() {
    }

}