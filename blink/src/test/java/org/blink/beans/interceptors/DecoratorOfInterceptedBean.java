package org.blink.beans.interceptors;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public class DecoratorOfInterceptedBean {

    @Inject @Delegate private DecoratedTransactionalBean delegate;

    public void doSomething() {
        TransactionHolder.writeToTransactionLog("Transaction decorated");
        delegate.doSomething();
    }
}
