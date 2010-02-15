package org.blink.beans;

import java.util.Set;

public interface BeanScanner {

    Set<Class<?>> findBeans();
}
