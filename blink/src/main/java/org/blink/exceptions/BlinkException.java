package org.blink.exceptions;

public class BlinkException extends RuntimeException {

    public BlinkException(Throwable t) {
        super(t);
    }

    public BlinkException(String message) {
        super(message);
    }

    public BlinkException(String message, Throwable t) {
        super(message, t);
    }

    public BlinkException() {
    }
}
