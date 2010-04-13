package org.blink.beans.events;

import javax.enterprise.event.Observes;
import javax.inject.Named;

import org.blink.beans.injection.First;

@Named
public class EventConsumer {

    private boolean eventHandled;
    private boolean unmachingQualifiersEventHandled;
    private boolean noQualifiersEventHandled;

    public void observes(@Observes @EventQualifier TestEvent event) {
        eventHandled = true;
    }

    public void observesUnmatching(@Observes @First TestEvent event) {
        unmachingQualifiersEventHandled = true;
    }
    public void observesNoQualifiers(@Observes TestEvent event) {
        noQualifiersEventHandled = true;
    }

    public boolean isEventHandled() {
        return eventHandled;
    }

    public void setEventHandled(boolean eventHandled) {
        this.eventHandled = eventHandled;
    }

    public boolean isUnmachingQualifiersEventHandled() {
        return unmachingQualifiersEventHandled;
    }

    public void setUnmachingQualifiersEventHandled(
            boolean unmachingQualifiersEventHandled) {
        this.unmachingQualifiersEventHandled = unmachingQualifiersEventHandled;
    }

    public boolean isNoQualifiersEventHandled() {
        return noQualifiersEventHandled;
    }

    public void setNoQualifiersEventHandled(boolean noQualifiersEventHandled) {
        this.noQualifiersEventHandled = noQualifiersEventHandled;
    }
}
