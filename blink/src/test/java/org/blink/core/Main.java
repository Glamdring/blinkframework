package org.blink.core;

import javax.enterprise.inject.spi.BeanManager;


public class Main {

    public static void main(String[] args) {
        Bootstrap b = new Bootstrap();
        BeanManager beanManager = b.initialize();
        
        beanManager.getBeans(null);

    }
}
