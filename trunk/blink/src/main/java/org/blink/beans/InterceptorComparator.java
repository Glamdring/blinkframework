package org.blink.beans;

import java.util.Comparator;

import javax.enterprise.inject.spi.Interceptor;

public class InterceptorComparator implements Comparator<Interceptor<?>> {

    public int compare(Interceptor<?> i1, Interceptor<?> i2) {
        return ((InterceptorBean) i1).getIndex()
                - ((InterceptorBean) i2).getIndex();
    }
}
