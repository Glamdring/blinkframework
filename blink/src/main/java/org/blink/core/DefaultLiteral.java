package org.blink.core;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;

public final class DefaultLiteral extends AnnotationLiteral<Default> implements
        Default {

    public static final Default INSTANCE = new DefaultLiteral();
}
