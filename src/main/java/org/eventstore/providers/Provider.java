package org.eventstore.providers;

import org.eventstore.models.Event;

import java.util.List;

public interface Provider {

    Event addEvent(String aggregation, String streamId, Event event);
    List<Event> getEvents(String aggregation, String streamId);
}
