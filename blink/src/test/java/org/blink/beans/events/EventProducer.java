package org.blink.beans.events;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class EventProducer {

    @Inject @EventQualifier
    private Event<TestEvent> event;

    public void fireEvent() {
        TestEvent evt = new TestEvent();
        event.fire(evt);
    }
}
