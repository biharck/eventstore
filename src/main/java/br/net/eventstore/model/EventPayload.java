package br.net.eventstore.model;

/**
 * The payload of an Event in the stream of events
 */
public class EventPayload {

    private String data;


    /**
     * Create a new Event with the given payload
     * @param data Any data associated
     */
    public EventPayload(String data) {
        this.data = data;
    }

    /**
     * Retrieve the event payload. The payload can be any data associated with the event
     * @return The event payload
     */
    public String getData() {
        return data;
    }
}
