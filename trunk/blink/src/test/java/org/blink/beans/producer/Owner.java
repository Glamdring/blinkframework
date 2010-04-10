package org.blink.beans.producer;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.blink.beans.injection.First;
import org.blink.beans.injection.Second;

public class Owner {

    @Produces @First private NonBean field = new NonBean() {};

    @Produces
    @Second
    @Named(NonBean.TEST_CUSTOM_NAME)
    @RequestScoped
    public NonBean createSampleBean() {
        return new NonBean() {
        };
    }
}
