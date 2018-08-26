package org.eventstore.model;

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
     */
    public Event(String payload) {
        this.payload = payload;
    }

    /**
     * Retrieve the event payload. The payload can be any data associated with the event
     * @return The event payload
     */
    public String getPayload() {
        return payload;
    }

    /**
     * Retrieve the sequence order for the event in the {@link org.eventstore.EventStream}
     * @return The order of the event in the stream
     */
    public long getSequence() {
        return sequence;
    }

    /**
     * Inform the sequence order for the event in the {@link org.eventstore.EventStream}
     * @param sequence The order of the event in the stream
     */
    public void setSequence(long sequence){
        this.sequence = sequence;
    }

    /**
     * Retrieve the time where the event was persisted in the {@link org.eventstore.EventStream}
     * @return When the event was persisted in the stream
     */
    public long getCommitTimestamp() {
        return commitTimestamp;
    }

    /**
     * Inform the time where the event was persisted in the {@link org.eventstore.EventStream}
     * @param commitTimestamp When the event was persisted in the stream
     */
    public void setCommitTimestamp(long commitTimestamp) {
        this.commitTimestamp = commitTimestamp;
    }
}
