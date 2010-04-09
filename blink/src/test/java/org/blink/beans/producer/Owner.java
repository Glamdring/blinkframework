package org.blink.beans.producer;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

public class Owner {

    @Produces @RequestScoped public NonBean createSampleBean() {
        return new NonBean() {
        };
    }
}
