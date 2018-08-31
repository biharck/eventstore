package br.net.eventstore;

import br.net.eventstore.model.Event;
import br.net.eventstore.model.Message;
import br.net.eventstore.provider.Provider;

import java.util.List;
import java.util.stream.Stream;

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
     * Retrieves a ranged list containing all the events in the stream in order.
     * @param offset The start position in the stream list
     * @param limit The desired quantity events
     * @return All the events
     */
    public Stream<Event> getEvents(int offset, int limit){
        return getProvider().getEvents(aggregation, streamId, offset, limit);
    }

    /**
     * Retrieves a list containing all the events in the stream in order.
     * @return All the events
     */
    public Stream<Event> getEvents(){
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
