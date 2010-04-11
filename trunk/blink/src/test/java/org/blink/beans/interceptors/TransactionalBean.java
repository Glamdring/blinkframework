package org.blink.beans.interceptors;

import javax.inject.Named;

@Named
public class TransactionalBean {

    @Transactional
    public void executeInTransaction() {
        TransactionHolder.writeToTransactionLog("Transaction in process");
    }
}
