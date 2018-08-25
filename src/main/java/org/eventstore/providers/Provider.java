package org.eventstore.providers;

import org.eventstore.models.Event;

import java.util.List;

/**
 * A Persistence provider for the {@link org.eventstore.EventStore}. It is responsible for write and read {@link Event}s
 * in the {@link org.eventstore.EventStream}
 */
public interface Provider {

    /**
     * Add a new {@link Event} in the {@link org.eventstore.EventStream}
     * @param aggregation The parent aggregation
     * @param streamId The {@link org.eventstore.EventStream} identifier
     * @param event The Event
     * @return The updated event, after persisted.
     */
    Event addEvent(String aggregation, String streamId, Event event);

    /**
     * Retrieve a list of events in the {@link org.eventstore.EventStream}
     * @param aggregation The parent aggregation
     * @param streamId The {@link org.eventstore.EventStream} identifier
     * @return A List with events in the {@link org.eventstore.EventStream}
     */
    List<Event> getEvents(String aggregation, String streamId);
}
