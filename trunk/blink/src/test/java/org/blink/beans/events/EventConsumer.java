package org.blink.beans.events;

import javax.enterprise.event.Observes;
import javax.inject.Named;

@Named
public class EventConsumer {

    private boolean eventHandled;

    public void observes(@Observes @EventQualifier TestEvent event) {
        eventHandled = true;
    }

    public boolean isEventHandled() {
        return eventHandled;
    }

    public void setEventHandled(boolean eventHandled) {
        this.eventHandled = eventHandled;
    }
}
