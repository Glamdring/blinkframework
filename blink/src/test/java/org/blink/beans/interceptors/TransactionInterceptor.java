package org.blink.beans.interceptors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor @Transactional
public class TransactionInterceptor {

    @AroundInvoke
    public Object handle(InvocationContext ctx) throws Exception {
        TransactionHolder.writeToTransactionLog("Transacton started");
        Object result = ctx.proceed();
        TransactionHolder.writeToTransactionLog("Transacton committed");
        return result;
    }
}
