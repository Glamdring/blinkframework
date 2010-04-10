package org.blink.exceptions;

import org.blink.types.BlinkAnnotatedType;

public class DefinitionException extends BlinkException {

    private BlinkAnnotatedType<?> type;

    public DefinitionException(String message, BlinkAnnotatedType<?> type) {
        super(message + " in " + type.getJavaClass().getName());
        this.type = type;
    }

    public DefinitionException(String message) {
        super(message);
    }

    public BlinkAnnotatedType<?> getType() {
        return type;
    }
    public void setType(BlinkAnnotatedType<?> type) {
        this.type = type;
    }
}
