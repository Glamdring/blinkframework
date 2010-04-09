package org.blink.beans.producer;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

public class Owner {

    @Produces
    @Named(NonBean.TEST_CUSTOM_NAME)
    @RequestScoped
    public NonBean createSampleBean() {
        return new NonBean() {
        };
    }
}
