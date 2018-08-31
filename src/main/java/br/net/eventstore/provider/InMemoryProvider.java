package br.net.eventstore.provider;

import br.net.eventstore.model.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * A Persistence Provider that handle all the data in memory. It is a very simple implementation that should be used
 * only for development and test purposes.
 */
public class InMemoryProvider implements Provider{

    private ConcurrentHashMap<String, ConcurrentHashMap<String, List<Event>>> store = new ConcurrentHashMap<>();
    private static Lock writeLock = new ReentrantLock();

    @Override
    public Event addEvent(String aggregation, String streamId, Event event){
        List<Event> currentEvents = getEventsList(aggregation, streamId);
        event.setCommitTimestamp(System.currentTimeMillis());

        addEvent(event, currentEvents);
        return event;
    }

    @Override
    public Stream<Event> getEvents(String aggregation, String streamId){
        return getEventsList(aggregation, streamId).stream();
    }

    @Override
    public Stream<Event> getEvents(String aggregation, String streamId, int offset, int limit) {
        return getEvents(aggregation, streamId).skip(offset).limit(limit);
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
        return Collections.list(store.get(aggregation).keys()).stream();

    }

    @Override
    public Stream<String> getStreams(String aggregation, int offset, int limit) {
        return getStreams(aggregation).sorted().skip(offset).limit(limit);
    }

    private void addEvent(Event event, List<Event> currentEvents) {
        writeLock.lock();
        try {

            event.setSequence(currentEvents.size());
            currentEvents.add(event);
        } finally {
             writeLock.unlock();
        }
    }

    private List<Event> getEventsList(String aggregation, String streamId) {
        ConcurrentHashMap<String, List<Event>> aggregateStreams = store.computeIfAbsent(aggregation, key -> new ConcurrentHashMap<>());
        return aggregateStreams.computeIfAbsent(streamId, key -> Collections.synchronizedList(new ArrayList<>()));
    }

}
