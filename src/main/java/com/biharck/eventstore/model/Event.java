package com.biharck.eventstore.model;

import com.biharck.eventstore.EventStream;

/**
 * An Event in the stream of events
 */
public class Event {

    private String payload;
    private long commitTimestamp;
    private long sequence;

    /**
     * Default Constructor
     */
    public Event() { }

    /**
     * Create a new Event with the given payload
     * @param payload Any data associated
     */
    public Event(String payload) {
        this();
        setPayload(payload);
    }

    /**
     * Retrieve the event payload. The payload can be any data associated with the event
     * @return The event payload
     */
    public String getPayload() {
        return payload;
    }

    /**
     * Inform the event payload. The payload can be any data associated with the event
     * @param payload The event payload
     */
    public void setPayload(String payload) { this.payload = payload; }

    /**
     * Retrieve the sequence order for the event in the {@link EventStream}
     * @return The order of the event in the stream
     */
    public long getSequence() {
        return sequence;
    }

    /**
     * Inform the sequence order for the event in the {@link EventStream}
     * @param sequence The order of the event in the stream
     */
    public void setSequence(long sequence){
        this.sequence = sequence;
    }

    /**
     * Retrieve the time where the event was persisted in the {@link EventStream}
     * @return When the event was persisted in the stream
     */
    public long getCommitTimestamp() {
        return commitTimestamp;
    }

    /**
     * Inform the time where the event was persisted in the {@link EventStream}
     * @param commitTimestamp When the event was persisted in the stream
     */
    public void setCommitTimestamp(long commitTimestamp) {
        this.commitTimestamp = commitTimestamp;
    }
}
