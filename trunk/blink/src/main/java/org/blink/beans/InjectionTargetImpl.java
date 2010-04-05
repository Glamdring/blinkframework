package org.blink.beans;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

public class InjectionTargetImpl<T> implements InjectionTarget<T> {

    @Override
    public void inject(T instance, CreationalContext<T> ctx)
    {
        //InjectionTargetBean<T> bean = getBean(InjectionTargetBean.class);

//        bean.injectResources(instance, ctx);
//        bean.injectFields(instance, ctx);
//        bean.injectMethods(instance, ctx);
    }

    @Override
    public void postConstruct(T instance)
    {
//        InjectionTargetBean<T> bean = getBean(InjectionTargetBean.class);

//        bean.postConstruct(instance);
    }

    @Override
    public void preDestroy(T instance)
    {
//        InjectionTargetBean<T> bean = getBean(InjectionTargetBean.class);

//        bean.preDestroy(instance);
    }

    @Override
    public void dispose(T paramT) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public T produce(CreationalContext<T> paramCreationalContext) {
        // TODO Auto-generated method stub
        return null;
    }

}
