package br.net.eventstore.model;

import br.net.eventstore.EventStream;

/**
 * An Event in the stream of events
 */
public class Event {

    private String payload;
    private long commitTimestamp;
    private long sequence;


    /**
     * Create a new Event with the given payload
     * @param payload Any data associated
     * @param commitTimestamp The timestamp when the event gets created
     * @param sequence The auto increment value which defines the event order
     */
    public Event(EventPayload payload, long commitTimestamp, long sequence) {
        this.payload = payload.getData();
        this.commitTimestamp = commitTimestamp;
        this.sequence = sequence;
    }

    /**
     * Retrieve the event payload. The payload can be any data associated with the event
     * @return The event payload
     */
    public String getPayload() {
        return payload;
    }

    /**
     * Retrieve the sequence order for the event in the {@link EventStream}
     * @return The order of the event in the stream
     */
    public long getSequence() {
        return sequence;
    }

    /**
     * Retrieve the time where the event was persisted in the {@link EventStream}
     * @return When the event was persisted in the stream
     */
    public long getCommitTimestamp() {
        return commitTimestamp;
    }

}
