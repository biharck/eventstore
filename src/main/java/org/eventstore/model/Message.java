package org.eventstore.model;

/**
 * A Meesage sent by a {@link org.eventstore.publisher.Publisher} to inform {@link org.eventstore.publisher.Subscriber}s
 * that new {@link Event}s was added to the {@link org.eventstore.EventStore}
 */
public class Message {
    private String aggregation;
    private String streamId;
    private Event event;

    /**
     * Inform the name of the parent aggregation
     * @param aggregation The parent Aggregation
     * @return The Message instance
     */
    public Message setAggregation(String aggregation) {
        this.aggregation = aggregation;
        return this;
    }

    /**
     * Inform the {@link org.eventstore.EventStream} identifier
     * @param streamId The {@link org.eventstore.EventStream} identifier
     * @return The Message instance
     */
    public Message setStreamId(String streamId) {
        this.streamId = streamId;
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
     * Retrieve the name of the parent aggregation
     * @return The parent aggregation
     */
    public String getAggregation() {
        return aggregation;
    }

    /**
     * Retrieve the {@link org.eventstore.EventStream} identifier
     * @return The stream identifier
     */
    public String getStreamId() {
        return streamId;
    }
}
