package org.blink.beans.interceptors;

import javax.inject.Named;

@Transactional
@Named
public class DecoratedTransactionalBean {
    public void doSomething() {
        TransactionHolder.writeToTransactionLog("In process");
    }
}
