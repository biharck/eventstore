package org.eventstore.models;

public class Message {
    private String aggregate;
    private String streamId;
    private Event event;

    public Message setAggregate(String aggregate) {
        this.aggregate = aggregate;
        return this;
    }

    public Message setStreamId(String streamId) {
        this.streamId = streamId;
        return this;
    }

    public Message setEvent(Event event) {
        this.event = event;
        return this;
    }

    public Event getEvent() { return event; }

    public String getAggregate() {
        return aggregate;
    }

    public String getStreamId() {
        return streamId;
    }
}
