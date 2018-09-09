package br.net.eventstore;

import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;
import br.net.eventstore.model.Message;
import br.net.eventstore.provider.PersistenceProvider;

import java.util.stream.Stream;

/**
 * An Event Stream
 */
public class EventStream  {

    private br.net.eventstore.model.Stream stream;
    private EventStore eventStore;

    EventStream(EventStore eventStore, br.net.eventstore.model.Stream stream){
        this.eventStore = eventStore;
        this.stream = stream;
    }

    /**
     * Retrieves a ranged list containing all the events in the stream in order.
     * @param offset The start position in the stream list
     * @param limit The desired quantity events
     * @return All the events
     */
    public Stream<Event> getEvents(int offset, int limit){
        return getProvider().getEvents(stream, offset, limit);
    }

    /**
     * Retrieves a list containing all the events in the stream in order.
     * @return All the events
     */
    public Stream<Event> getEvents(){
        return getProvider().getEvents(stream);
    }

    /**
     * Add a new event to the end of the event stream.
     * @param payload The event payload
     * @return The event, updated with information like its sequence order and commitTimestamp
     */
    public Event addEvent(EventPayload payload) {
        Event addedEvent = getProvider().addEvent(stream, payload);
        if (eventStore.getPublisher() != null) {
            Message message = new Message().setStream(stream).setEvent(addedEvent);
            eventStore.getPublisher().publish(message);
        }
        return addedEvent;
    }

    /**
     * Retrieve the parent aggregation for this event stream
     * @return The parent aggregation
     */
    public String getAggregation() {
        return stream.getAggregation();
    }

    /**
     * Retrieve the event stream identifier
     * @return The event stream identifier
     */
    public String getStreamId() {
        return stream.getId();
    }

    private PersistenceProvider getProvider() {
        assert(eventStore.getProvider() != null): "No Provider configured in EventStore.";
        return eventStore.getProvider();
    }
}
