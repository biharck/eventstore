package org.eventstore;

import org.eventstore.models.Event;
import org.eventstore.providers.Provider;

import java.util.List;

public class EventStream  {

    private String streamId;
    private String aggregate;
    private Provider provider;

    EventStream(Provider provider, String aggregate, String streamId){
        this.provider = provider;
        this.aggregate = aggregate;
        this.streamId = streamId;
    }

    public List<Event> getEvents(){

        return provider.getEvents(aggregate, streamId);
    }

    public Event addEvent(Event event){
        return provider.addEvent(aggregate, streamId, event);
    }

    public String getAggregate() {
        return aggregate;
    }

    public String getStreamId() {
        return streamId;
    }
}
