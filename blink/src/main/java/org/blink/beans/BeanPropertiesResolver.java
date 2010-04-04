package org.blink.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

public interface BeanPropertiesResolver {

    public Set<InjectionPoint> getInjectionPoints(Bean<?> bean);

    public String getName(Bean<?> bean);

    public Set<Annotation> getQualifiers(Bean<?> bean);

    public Class<? extends Annotation> getScope(Bean<?> bean);

    public Set<Class<? extends Annotation>> getStereotypes(Bean<?> bean);

    public Set<Type> getTypes(Bean<?> bean);

    public boolean isAlternative(Bean<?> bean);

    public boolean isNullable(Bean<?> bean);
}
