package br.net.eventstore.provider.sql;

import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;
import br.net.eventstore.provider.PersistenceProvider;

import java.util.stream.Stream;

public class SQLProvider implements PersistenceProvider {

    @Override
    public Event addEvent(br.net.eventstore.model.Stream stream, EventPayload payload) {

        Event newEvent = new Event(payload.getData(), System.currentTimeMillis(), 1);

        return newEvent;
    }

    @Override
    public Stream<Event> getEvents(br.net.eventstore.model.Stream stream) {
        return null;
    }

    @Override
    public Stream<Event> getEvents(br.net.eventstore.model.Stream stream, int offset, int limit) {
        return null;
    }

    @Override
    public Stream<String> getAggregations() {
        return null;
    }

    @Override
    public Stream<String> getAggregations(int offset, int limit) {
        return null;
    }

    @Override
    public Stream<String> getStreams(String aggregation) {
        return null;
    }

    @Override
    public Stream<String> getStreams(String aggregation, int offset, int limit) {
        return null;
    }
}
