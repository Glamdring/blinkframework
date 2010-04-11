package org.blink.beans;

public class BeanClassDescriptor {

    private Class<?> beanClass;
    private int index; // for decorators and interceptors

    public BeanClassDescriptor(Class<?> beanClass, int index) {
        super();
        this.beanClass = beanClass;
        this.index = index;
    }
    public BeanClassDescriptor(Class<?> beanClass) {
        super();
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int order) {
        this.index = order;
    }


}
