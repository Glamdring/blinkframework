package org.blink.beans.producer;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class Consumer {

    @Inject
    private NonBean nonBean;

    public NonBean getNonBean() {
        return nonBean;
    }

    public void setNonBean(NonBean nonBean) {
        this.nonBean = nonBean;
    }
}
