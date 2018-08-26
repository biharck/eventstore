package com.biharck.eventstore.provider;

import com.biharck.eventstore.EventStore;
import com.biharck.eventstore.EventStream;
import com.biharck.eventstore.model.Event;

import java.util.List;

/**
 * A Persistence provider for the {@link EventStore}. It is responsible for write and read {@link Event}s
 * in the {@link EventStream}
 */
public interface Provider {

    /**
     * Add a new {@link Event} in the {@link EventStream}
     * @param aggregation The parent aggregation
     * @param streamId The {@link EventStream} identifier
     * @param event The Event
     * @return The updated event, after persisted.
     */
    Event addEvent(String aggregation, String streamId, Event event);

    /**
     * Retrieve a list of events in the {@link EventStream}
     * @param aggregation The parent aggregation
     * @param streamId The {@link EventStream} identifier
     * @return A List with events in the {@link EventStream}
     */
    List<Event> getEvents(String aggregation, String streamId);
}
