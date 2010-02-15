package org.blink.exceptions;

public class ContextInitializationException extends BlinkException {

    public ContextInitializationException() {
        super();
    }

    public ContextInitializationException(Throwable t) {
        super(t);
    }

    public ContextInitializationException(String message) {
        super(message);
    }
}
