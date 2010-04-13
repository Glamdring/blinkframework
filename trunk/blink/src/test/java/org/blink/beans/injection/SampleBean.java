package org.blink.beans.injection;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SampleBean {

    @Inject @First
    private InterfaceToInject fieldFirst;

    private InterfaceToInject initializerFirst;

    private InterfaceToInject constructorSecond;

    @Inject @Any
    private Instance<InterfaceToInject> instance;

    @Inject
    private Instance<InterfaceToInject> instance2;

    private boolean initialized;

    @Inject
    public void initialize(@First BeanToInject f) {
        this.initializerFirst = f;
    }

    @Inject
    public SampleBean(@Second InterfaceToInject beanToInject) {
        this.constructorSecond = beanToInject;
    }

    @PostConstruct
    public void init() {
        initialized = true;
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

    public InterfaceToInject getInstanceInstance() {
        return instance.select(BeanToInject.class).get();
    }

    public Instance<InterfaceToInject> getInstance() {
        return instance;
    }

    public void setInstance(Instance<InterfaceToInject> instance) {
        this.instance = instance;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public Instance<InterfaceToInject> getInstance2() {
        return instance2;
    }

    public void setInstance2(Instance<InterfaceToInject> instance2) {
        this.instance2 = instance2;
    }
}
