package br.net.eventstore.provider;

import br.net.eventstore.EventStore;
import br.net.eventstore.EventStream;
import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;

import java.util.stream.Stream;

/**
 * A Persistence provider for the {@link EventStore}. It is responsible for write and read {@link Event}s
 * in the {@link EventStream}
 */
public interface PersistenceProvider {

    /**
     * Add a new {@link Event} in the {@link EventStream}
     * @param stream The associated stream
     * @param payload The Event payload
     * @return The updated event, after persisted.
     */
    Event addEvent(br.net.eventstore.model.Stream stream, EventPayload payload);

    /**
     * Retrieves a list of events in the {@link EventStream}
     * @param stream The associated stream
     * @return A List with events in the {@link EventStream}
     */
    Stream<Event> getEvents(br.net.eventstore.model.Stream stream);

    /**
     * Retrieves a ranged list of events in the {@link EventStream}
     * @param stream The associated stream
     * @param offset The start position in the events list
     * @param limit The desired quantity events
     * @return A List with events in the {@link EventStream}
     */
    Stream<Event> getEvents(br.net.eventstore.model.Stream stream, int offset, int limit);

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
     * @param aggregation The aggregation
     * @return The stream list
     */
    Stream<String> getStreams(String aggregation);

    /**
     * Retrieves a ranged stream list
     * @param aggregation The aggregation
     * @param offset The start position in the stream list
     * @param limit The desired quantity streams
     * @return The stream list
     */
    Stream<String> getStreams(String aggregation, int offset, int limit);
}
