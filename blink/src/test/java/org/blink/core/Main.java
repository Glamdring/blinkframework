package org.blink.core;

import javax.enterprise.inject.spi.Bean;

import org.blink.beans.CreationalContextImpl;



public class Main {

    public static void main(String[] args) {
        BeanDeployer b = new BeanDeployer();
        b.deploy();

        Bean bean = b.getBeanManager().getBeans("sampleBean").iterator().next();
        SampleBean sb = (SampleBean) bean.create(new CreationalContextImpl<SampleBean>(bean));

        System.out.println(sb.getBeanToInject());
    }
}
