package org.blink.beans.decorators;

import javax.inject.Named;

@Named
public class DecoratedBean implements DecorationInterface {

    private int calls = 0;

    public void doSomething() {
        incrementCalls();
    }

    protected void incrementCalls() {
        calls++;
    }

    public int getCalls() {
        return calls;
    }
}
