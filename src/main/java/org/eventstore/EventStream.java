package org.eventstore;

import org.eventstore.models.Event;
import org.eventstore.models.Message;
import org.eventstore.providers.Provider;

import java.util.List;

public class EventStream  {

    private String streamId;
    private String aggregate;
    private EventStore eventStore;

    EventStream(EventStore eventStore, String aggregate, String streamId){
        this.eventStore = eventStore;
        this.aggregate = aggregate;
        this.streamId = streamId;
    }

    public List<Event> getEvents(){

        return getProvider().getEvents(aggregate, streamId);
    }

    public Event addEvent(Event event){
        Event addedEvent = getProvider().addEvent(aggregate, streamId, event);
        if (eventStore.getPublisher() != null) {
            Message message = new Message().setAggregate(aggregate).setStreamId(streamId).setEvent(event);
            eventStore.getPublisher().publish(message);
        }
        return addedEvent;
    }

    public String getAggregate() {
        return aggregate;
    }

    public String getStreamId() {
        return streamId;
    }

    private Provider getProvider() {
        assert(eventStore.getProvider() != null): "No Provider configured in EventStore.";
        return eventStore.getProvider();
    }
}
