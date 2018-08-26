package io.github.biharck.eventstore.provider;

import io.github.biharck.eventstore.model.Event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A Persistence Provider that handle all the data in memory. It is a very simple implementation that should be used
 * only for development and test purposes.
 */
public class InMemoryProvider implements Provider{

    private ConcurrentHashMap<String, ConcurrentHashMap<String, List<Event>>> store = new ConcurrentHashMap<>();
    private static Lock writeLock = new ReentrantLock();

    @Override
    public Event addEvent(String aggregation, String streamId, Event event){
        List<Event> currentEvents = getEvents(aggregation, streamId);
        event.setCommitTimestamp(System.currentTimeMillis());

        if (addEvent(event, currentEvents)) {
            return event;
        }
        return null;
    }

    @Override
    public List<Event> getEvents(String aggregation, String streamId){
        ConcurrentHashMap<String, List<Event>> aggregateStreams = store.computeIfAbsent(aggregation, key -> new ConcurrentHashMap<>());
        return aggregateStreams.computeIfAbsent(streamId, key -> Collections.synchronizedList(new ArrayList<>()));
    }

    private boolean addEvent(Event event, List<Event> currentEvents) {
        writeLock.lock();
        try {
            event.setSequence(currentEvents.size());
            return currentEvents.add(event);
        } finally {
             writeLock.unlock();
        }
    }
}
