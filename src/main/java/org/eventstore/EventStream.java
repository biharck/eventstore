package org.eventstore;

import org.eventstore.model.Event;
import org.eventstore.model.Message;
import org.eventstore.provider.Provider;

import java.util.List;

/**
 * An Event Stream
 */
public class EventStream  {

    private String streamId;
    private String aggregation;
    private EventStore eventStore;

    EventStream(EventStore eventStore, String aggregation, String streamId){
        this.eventStore = eventStore;
        this.aggregation = aggregation;
        this.streamId = streamId;
    }

    /**
     * Rertieve a list containing all the events in the stream in order.
     * @return All the events
     */
    public List<Event> getEvents(){

        return getProvider().getEvents(aggregation, streamId);
    }

    /**
     * Add a new event to the end of the event stream.
     * @param event The event
     * @return The event, updated with informations like its sequence order and commitTimestamp
     */
    public Event addEvent(Event event){
        Event addedEvent = getProvider().addEvent(aggregation, streamId, event);
        if (eventStore.getPublisher() != null) {
            Message message = new Message().setAggregation(aggregation).setStreamId(streamId).setEvent(addedEvent);
            eventStore.getPublisher().publish(message);
        }
        return addedEvent;
    }

    /**
     * Retrieve the parent aggregation for this event stream
     * @return The parent aggregation
     */
    public String getAggregation() {
        return aggregation;
    }

    /**
     * Retrieve the event stream identifier
     * @return The event stream identifier
     */
    public String getStreamId() {
        return streamId;
    }

    private Provider getProvider() {
        assert(eventStore.getProvider() != null): "No Provider configured in EventStore.";
        return eventStore.getProvider();
    }
}
