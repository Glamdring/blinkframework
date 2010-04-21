package org.blink.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class RequestListener implements ServletRequestListener {

    private static ThreadLocal<ServletContext> currentContext = new ThreadLocal<ServletContext>();

    @Override
    public void requestDestroyed(ServletRequestEvent arg0) {
        currentContext.set(null);

    }

    @Override
    public void requestInitialized(ServletRequestEvent evt) {
        currentContext.set(evt.getServletContext());
    }

    public static ThreadLocal<ServletContext> getCurrentContext() {
        return currentContext;
    }

    public static void setCurrentContext(ThreadLocal<ServletContext> currentContext) {
        RequestListener.currentContext = currentContext;
    }
}
