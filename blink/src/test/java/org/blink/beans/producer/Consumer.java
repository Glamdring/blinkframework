package org.blink.beans.producer;

import javax.inject.Inject;
import javax.inject.Named;

import org.blink.beans.injection.First;
import org.blink.beans.injection.Second;

@Named
public class Consumer {

    @Inject
    @Second
    private NonBean nonBeanSecond;

    @Inject
    @First
    private NonBean nonBeanFirst;

    public NonBean getNonBeanSecond() {
        return nonBeanSecond;
    }

    public void setNonBeanSecond(NonBean nonBean) {
        this.nonBeanSecond = nonBean;
    }

    public NonBean getNonBeanFirst() {
        return nonBeanFirst;
    }

    public void setNonBeanFirst(NonBean nonBeanFirst) {
        this.nonBeanFirst = nonBeanFirst;
    }
}
