package org.blink.core;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;

public class DefaultLiteral extends AnnotationLiteral<Default> implements
        Default {

    public static final Default INSTANCE = new DefaultLiteral();
}
