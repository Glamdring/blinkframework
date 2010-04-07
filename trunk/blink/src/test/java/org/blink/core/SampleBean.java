package org.blink.core;

import javax.inject.Named;

@Named
public class SampleBean {

    public SampleBean() {
        System.out.println("Constructor");
    }
}
