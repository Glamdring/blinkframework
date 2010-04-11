package org.blink.beans.interceptors;

import java.util.ArrayList;
import java.util.List;

public class TransactionHolder {

    private static ThreadLocal<List<String>> transactionLog = new ThreadLocal<List<String>>();

    static {
        transactionLog.set(new ArrayList<String>());
    }

    public static List<String> getCurrentTrsanctionLog() {
        return transactionLog.get();
    }

    public static void writeToTransactionLog(String message) {
        transactionLog.get().add(message);
    }
}
