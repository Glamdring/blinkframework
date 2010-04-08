package org.blink.core;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SampleBean {

    @Inject
    private BeanToInject field;

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

    public BeanToInject getField() {
        return field;
    }

    public void setField(BeanToInject field) {
        this.field = field;
    }
}
