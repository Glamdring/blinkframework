package org.blink.beans.events;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class EventProducer {

    @Inject @EventQualifier
    private Event<SampleEvent> event;

    public void fireEvent() {
        SampleEvent evt = new SampleEvent();
        event.fire(evt);
    }
}
