package org.blink.beans;

import javax.enterprise.inject.spi.Bean;

import org.blink.beans.injection.SampleBean;
import org.blink.core.BeanDeployer;

public class Main {

    public static void main(String[] args) {
        BeanDeployer b = new BeanDeployer();
        b.deploy();

        Bean bean = b.getBeanManager().getBeans("sampleBean").iterator().next();
        SampleBean sb = (SampleBean) bean.create(new CreationalContextImpl<SampleBean>(bean));

    }
}
