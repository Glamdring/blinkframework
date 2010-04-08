package org.blink.core;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SampleBean {

    private BeanToInject beanToInject;

    @Inject
    public SampleBean(BeanToInject beanToInject) {
        this.beanToInject = beanToInject;
    }

    public BeanToInject getBeanToInject() {
        return beanToInject;
    }

    public void setBeanToInject(BeanToInject beanToInject) {
        this.beanToInject = beanToInject;
    }
}
