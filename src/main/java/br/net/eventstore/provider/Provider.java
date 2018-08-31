package br.net.eventstore.provider;

import br.net.eventstore.EventStore;
import br.net.eventstore.EventStream;
import br.net.eventstore.model.Event;

import java.util.List;
import java.util.stream.Stream;

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
     * Retrieves a list of events in the {@link EventStream}
     * @param aggregation The parent aggregation
     * @param streamId The {@link EventStream} identifier
     * @return A List with events in the {@link EventStream}
     */
    Stream<Event> getEvents(String aggregation, String streamId);

    /**
     * Retrieves a ranged list of events in the {@link EventStream}
     * @param aggregation The parent aggregation
     * @param streamId The {@link EventStream} identifier
     * @param offset The start position in the events list
     * @param limit The desired quantity events
     * @return A List with events in the {@link EventStream}
     */
    Stream<Event> getEvents(String aggregation, String streamId, int offset, int limit);

    /**
     * Retrieves the aggregation list
     * @return The aggregation list
     */
    Stream<String> getAggregations();

    /**
     * Retrieves a ranged aggregation list
     * @param offset The start position in the aggregation list
     * @param limit The desired quantity aggregations
     * @return The aggregation list
     */
    Stream<String> getAggregations(int offset, int limit);

    /**
     * Retrieves the stream list
     * @return The stream list
     */
    Stream<String> getStreams(String aggregation);

    /**
     * Retrieves a ranged stream list
     * @param offset The start position in the stream list
     * @param limit The desired quantity streams
     * @return The stream list
     */
    Stream<String> getStreams(String aggregation, int offset, int limit);
}
