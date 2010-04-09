package org.blink.tests;

import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import junit.framework.Assert;

import org.blink.beans.CreationalContextImpl;
import org.blink.beans.injection.BeanToInject;
import org.blink.beans.injection.SampleBean;
import org.blink.beans.injection.SecondBeanToInject;
import org.blink.beans.stereotypes.StereotypedBean;
import org.blink.core.BeanDeployer;
import org.junit.Test;

public class BeanManagerTest {

    private BeanManager deployAndGetManager() {
        BeanDeployer b = new BeanDeployer();
        b.deploy();
        return b.getBeanManager();
    }

    @SuppressWarnings("unchecked")
    private Object getBean(String name) {
        Bean bean = deployAndGetManager().getBeans(name).iterator().next();
        Object b = bean.create(new CreationalContextImpl<SampleBean>(bean));
        return b;
    }


    @Test
    public void injectionAndQualifiersTest() {
        SampleBean sb = (SampleBean) getBean("sampleBean");
        Assert.assertEquals(sb.getFieldFirst().getClass(), BeanToInject.class);
        Assert.assertEquals(sb.getInitializerFirst().getClass(), BeanToInject.class);
        Assert.assertEquals(sb.getConstructorSecond().getClass(), SecondBeanToInject.class);
    }

    @Test
    public void stereotypesTest() {
        Set<Bean<?>> beans = deployAndGetManager().getBeans(StereotypedBean.class);

        Assert.assertTrue("No beans of the defined type found", beans.size() != 0);

        Bean<?> bean = beans.iterator().next();

        Assert.assertTrue("No stereotypes set", bean.getStereotypes().size() == 1);
        Assert.assertNotNull("Name not set", bean.getName());
        Assert.assertEquals("Incorrect scope", bean.getScope(), RequestScoped.class);
    }
}
