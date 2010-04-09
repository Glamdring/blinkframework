package org.blink.beans.injection;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SampleBean {

    @Inject @First
    private InterfaceToInject fieldFirst;

    private InterfaceToInject initializerFirst;

    private InterfaceToInject constructorSecond;

    @Inject
    public void initialize(@First BeanToInject f) {
        this.initializerFirst = f;
    }

    @Inject
    public SampleBean(@Second InterfaceToInject beanToInject) {
        this.constructorSecond = beanToInject;
    }

    public InterfaceToInject getFieldFirst() {
        return fieldFirst;
    }

    public void setFieldFirst(InterfaceToInject fieldFirst) {
        this.fieldFirst = fieldFirst;
    }

    public InterfaceToInject getInitializerFirst() {
        return initializerFirst;
    }

    public void setInitializerFirst(InterfaceToInject initializerFirst) {
        this.initializerFirst = initializerFirst;
    }

    public InterfaceToInject getConstructorSecond() {
        return constructorSecond;
    }

    public void setConstructorSecond(InterfaceToInject constructorSecond) {
        this.constructorSecond = constructorSecond;
    }
}
