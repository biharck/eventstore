package org.eventstore.providers;

import org.eventstore.models.Event;

import java.util.List;

public class InMemoryProvider implements Provider{

    @Override
    public List addEvent(String streamId, Event event, String aggregation) {
        return null;
    }

    @Override
    public List getEventStream(String streamId) {
        return null;
    }
}
