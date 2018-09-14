package br.net.eventstore.provider;

import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * A Persistence Provider that handle all the data in memory. It is a very simple implementation that should be used
 * only for development and test purposes.
 */
public class InMemoryProvider implements PersistenceProvider{

    private ConcurrentHashMap<String, ConcurrentHashMap<String, List<Event>>> store = new ConcurrentHashMap<>();
    private static Lock writeLock = new ReentrantLock();

    @Override
    public Event addEvent(br.net.eventstore.model.Stream stream, EventPayload payload){
        List<Event> currentEvents = getEventsList(stream);

        writeLock.lock();
        try {

            Event newEvent = new Event(payload, System.currentTimeMillis(), currentEvents.size());
            currentEvents.add(newEvent);
            return newEvent;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Stream<Event> getEvents(br.net.eventstore.model.Stream stream){
        return getEventsList(stream).stream();
    }

    @Override
    public Stream<Event> getEvents(br.net.eventstore.model.Stream stream, int offset, int limit) {
        return getEvents(stream).skip(offset).limit(limit);
    }

    @Override
    public Stream<String> getAggregations() {
        return Collections.list(store.keys()).stream();
    }

    @Override
    public Stream<String> getAggregations(int offset, int limit) {
        return getAggregations().sorted().skip(offset).limit(limit);

    }

    @Override
    public Stream<String> getStreams(String aggregation) {
        ConcurrentHashMap streams = store.get(aggregation);
        if (streams != null) {
            return Collections.list(streams.keys()).stream();
        }
        return Stream.empty();

    }

    @Override
    public Stream<String> getStreams(String aggregation, int offset, int limit) {
        return getStreams(aggregation).sorted().skip(offset).limit(limit);
    }

    private List<Event> getEventsList(br.net.eventstore.model.Stream stream) {
        ConcurrentHashMap<String, List<Event>> aggregateStreams = store.computeIfAbsent(stream.getAggregation(), key -> new ConcurrentHashMap<>());
        return aggregateStreams.computeIfAbsent(stream.getId(), key -> Collections.synchronizedList(new ArrayList<>()));
    }

}
