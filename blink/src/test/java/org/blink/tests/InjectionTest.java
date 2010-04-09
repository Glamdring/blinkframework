package org.blink.tests;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import junit.framework.Assert;

import org.blink.beans.CreationalContextImpl;
import org.blink.core.BeanDeployer;
import org.junit.Test;

public class InjectionTest {

    private BeanManager deployAndGetManager() {
        BeanDeployer b = new BeanDeployer();
        b.deploy();
        return b.getBeanManager();
    }

    @SuppressWarnings("unchecked")
    private SampleBean getSampleBean() {
        Bean bean = deployAndGetManager().getBeans("sampleBean").iterator().next();
        SampleBean sb = (SampleBean) bean.create(new CreationalContextImpl<SampleBean>(bean));
        return sb;
    }


    @Test
    public void injectionAndQualifiersTest() {
        SampleBean sb = getSampleBean();
        Assert.assertEquals(sb.getFieldFirst().getClass(), BeanToInject.class);
        Assert.assertEquals(sb.getInitializerFirst().getClass(), BeanToInject.class);
        Assert.assertEquals(sb.getConstructorSecond().getClass(), SecondBeanToInject.class);
    }
}
