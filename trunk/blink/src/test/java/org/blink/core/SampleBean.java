package org.blink.core;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SampleBean {

    @Inject @First
    private InterfaceToInject field;

    private InterfaceToInject fieldByInitializer;

    private InterfaceToInject beanToInject;

    @Inject
    public void initialize(@First BeanToInject f) {
        this.fieldByInitializer = f;
    }

    @Inject
    public SampleBean(@Second InterfaceToInject beanToInject) {
        this.beanToInject = beanToInject;
    }

    public InterfaceToInject getField() {
        return field;
    }

    public void setField(InterfaceToInject field) {
        this.field = field;
    }

    public InterfaceToInject getFieldByInitializer() {
        return fieldByInitializer;
    }

    public void setFieldByInitializer(InterfaceToInject fieldByInitializer) {
        this.fieldByInitializer = fieldByInitializer;
    }

    public InterfaceToInject getBeanToInject() {
        return beanToInject;
    }

    public void setBeanToInject(InterfaceToInject beanToInject) {
        this.beanToInject = beanToInject;
    }

}
