package org.eventstore.providers;

import org.eventstore.models.Event;

import java.util.List;

public interface Provider {

    List addEvent(String streamId, Event event, String aggregation);
    List getEventStream(String streamId);
}
