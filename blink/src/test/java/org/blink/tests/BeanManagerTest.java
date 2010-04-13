package org.blink.tests;

import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.util.AnnotationLiteral;

import junit.framework.Assert;

import org.blink.beans.BlinkBean;
import org.blink.beans.decorators.DecoratedBean;
import org.blink.beans.events.EventConsumer;
import org.blink.beans.events.EventProducer;
import org.blink.beans.injection.BeanToInject;
import org.blink.beans.injection.First;
import org.blink.beans.injection.SampleBean;
import org.blink.beans.injection.Second;
import org.blink.beans.injection.SecondBeanToInject;
import org.blink.beans.interceptors.DecoratedTransactionalBean;
import org.blink.beans.interceptors.TransactionHolder;
import org.blink.beans.interceptors.TransactionalBean;
import org.blink.beans.producer.Consumer;
import org.blink.beans.producer.NonBean;
import org.blink.beans.stereotypes.StereotypedBean;
import org.blink.core.BeanDeployer;
import org.blink.exceptions.BlinkException;
import org.junit.Test;

public class BeanManagerTest {

    private BeanManager deployAndGetManager() {
        BeanDeployer b = new BeanDeployer();
        b.deploy();
        return b.getBeanManager();
    }

    private Object getBean(String name) {
        return getBean(deployAndGetManager(), name);
    }

    @SuppressWarnings("unchecked")
    private Object getBean(BeanManager manager, String name) {
        Bean bean = manager.getBeans(name).iterator().next();
        Object b = manager.getReference(bean, bean.getBeanClass(), manager.createCreationalContext(bean));
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

    @Test
    public void procuderMethodTest() {
        BeanManager manager = deployAndGetManager();
        Consumer consumer = (Consumer) getBean(manager, "consumer");
        NonBean nb = consumer.getNonBeanSecond();
        Assert.assertNotNull(
            "The NonBean is epxected to be generated and injected based on the procuder",
            nb);

        Set<Bean<?>> beans = manager.getBeans(NonBean.class, new AnnotationLiteral<Second>(){});

        Bean<?> bean = beans.iterator().next();
        Assert.assertEquals("Incorrect scope", RequestScoped.class, bean.getScope());
        Assert.assertEquals(NonBean.TEST_CUSTOM_NAME, bean.getName());
    }


    @Test
    public void procuderFieldTest() {
        BeanManager manager = deployAndGetManager();
        Consumer consumer = (Consumer) getBean(manager, "consumer");
        NonBean nb = consumer.getNonBeanFirst();
        Assert.assertNotNull(
            "The NonBean is epxected to be generated and injected based on the procuder",
            nb);

        Set<Bean<?>> beans = manager.getBeans(NonBean.class, new AnnotationLiteral<First>(){});

        Bean<?> bean = beans.iterator().next();
        Assert.assertEquals("Incorrect scope", ApplicationScoped.class, bean.getScope());
        Assert.assertNull("Name must be null", bean.getName());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void decoratorTest() {

        BeanManager manager = deployAndGetManager();

        Set<Bean<?>> beans = manager.getBeans(DecoratedBean.class);
        List<Decorator> decorators = ((BlinkBean) beans.iterator().next()).getDecorators();

        Assert.assertEquals(1, decorators.size());

        DecoratedBean decoratedBean = (DecoratedBean) getBean(manager, "decoratedBean");
        decoratedBean.doSomething();
        Assert.assertEquals(2, decoratedBean.getCalls());
    }

    @Test
    public void interceptorTest() {
        TransactionalBean transactionalBean = (TransactionalBean) getBean("transactionalBean");
        transactionalBean.executeInTransaction();

        // expecting all 3 messages - start, in process, commited - to be in the log
        Assert.assertEquals(3, TransactionHolder.getCurrentTrsanctionLog().size());
        TransactionHolder.getCurrentTrsanctionLog().clear();
    }

    @Test
    public void combinedInterceptorAndDelegateTest() {
        DecoratedTransactionalBean transactionalBean = (DecoratedTransactionalBean) getBean("decoratedTransactionalBean");
        transactionalBean.doSomething();

        // expecting all 4 messages - start, decorated, in process, commited - to be in the log
        Assert.assertEquals(4, TransactionHolder.getCurrentTrsanctionLog().size());

        List<String> msgs = TransactionHolder.getCurrentTrsanctionLog();
        Assert.assertEquals("Decorator message on incorrect position", "Transaction decorated", msgs.get(1));

        TransactionHolder.getCurrentTrsanctionLog().clear();
    }

    @Test
    public void instanceTest() {
        SampleBean bean = (SampleBean) getBean("sampleBean");
        Assert.assertEquals(BeanToInject.class, bean.getInstanceInstance().getClass());
    }

    @Test(expected=BlinkException.class)
    public void instanceExceptionTest() {
        SampleBean bean = (SampleBean) getBean("sampleBean");
        bean.getInstance2().select(new AnnotationLiteral<First>(){});
    }

    @Test
    public void postConstructTest() {
        SampleBean bean = (SampleBean) getBean("sampleBean");
        Assert.assertTrue(bean.isInitialized());
    }

    @Test
    public void eventTest() {
        BeanManager manager = deployAndGetManager();
        EventProducer eventProducer = (EventProducer) getBean(manager, "eventProducer");
        eventProducer.fireEvent();

        EventConsumer consumer = (EventConsumer) getBean(manager, "eventConsumer");

        Assert.assertTrue("Event hasn't been consumed", consumer
                .isEventHandled());
        Assert.assertFalse("Observer with no qualifiers consumed the event",
                consumer.isNoQualifiersEventHandled());
        Assert.assertFalse(
                        "Observer with mismatched qualifiers has wrongly consumed the event",
                        consumer.isUnmachingQualifiersEventHandled());
    }
}
