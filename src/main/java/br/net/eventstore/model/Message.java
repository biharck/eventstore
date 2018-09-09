package br.net.eventstore.model;

import br.net.eventstore.publisher.Publisher;
import br.net.eventstore.EventStore;
import br.net.eventstore.EventStream;
import br.net.eventstore.publisher.Subscriber;

/**
 * A Meesage sent by a {@link Publisher} to inform {@link Subscriber}s
 * that new {@link Event}s was added to the {@link EventStore}
 */
public class Message {
    private Stream stream;
    private Event event;

    /**
     * Inform the associated stream
     * @param stream The associated stream
     * @return The Message instance
     */
    public Message setStream(Stream stream) {
        this.stream = stream;
        return this;
    }

    /**
     * Inform the {@link Event} that was added to the stream
     * @param event The added event
     * @return The Message instance
     */
    public Message setEvent(Event event) {
        this.event = event;
        return this;
    }

    /**
     * Retrieve the {@link Event} that was added to the stream
     * @return The added event
     */
    public Event getEvent() { return event; }

    /**
     * Retrieve the associated stream
     * @return The associated stream
     */
    public Stream getStream() {
        return stream;
    }
}
