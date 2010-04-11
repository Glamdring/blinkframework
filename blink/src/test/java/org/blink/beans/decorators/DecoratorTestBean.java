package org.blink.beans.decorators;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public class DecoratorTestBean implements DecorationInterface {

    @Inject @Delegate private DecoratedBean delegate;

    public void doSomething() {
        delegate.incrementCalls();
        delegate.doSomething();
    }
}
