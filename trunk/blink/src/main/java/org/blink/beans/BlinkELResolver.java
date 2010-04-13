package org.blink.beans;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.enterprise.inject.spi.Bean;

public class BlinkELResolver extends ELResolver {

    private ConfigurableBeanManager beanManager;

    public BlinkELResolver(ConfigurableBeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext arg0, Object arg1) {
        return null;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext arg0,
            Object arg1) {
        return null;
    }

    @Override
    public Class<?> getType(ELContext arg0, Object arg1, Object arg2) {
        return null;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null) {
            String propertyString = property.toString();
            // this means the property is the name of the bean

            Set<Bean<?>> beans = getBeanManager().getBeans(propertyString);
            // TODO how to chose, if more than one exist?
            if (beans.size() == 0) {
                return null;
            }
            Bean<?> bean = beans.iterator().next();
            Object beanInstance = getBeanManager().getReference(bean, bean.getBeanClass(),
                    getBeanManager().createCreationalContext(bean));

            context.setPropertyResolved(true);
            return beanInstance;
        }
        return null;
    }

    @Override
    public boolean isReadOnly(ELContext arg0, Object arg1, Object arg2) {
        return false;
    }

    @Override
    public void setValue(ELContext arg0, Object arg1, Object arg2, Object arg3) {

    }

    protected ConfigurableBeanManager getBeanManager() {
        return beanManager;
    }

}
