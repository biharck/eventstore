package br.net.eventstore.model;

import br.net.eventstore.EventStream;

/**
 * An Event in the stream of events
 */
public class Stream {

    private String aggregation;
    private String id;

    /**
     * Create a new Stream
     * @param aggregation The parent aggregation
     * @param id The stream identifier
     */
    public Stream(String aggregation, String id) {
        this.aggregation = aggregation;
        this.id = id;
    }

    /**
     * Retrieve the name of the parent aggregation
     * @return The parent aggregation
     */
    public String getAggregation() {
        return aggregation;
    }

    /**
     * Retrieve the {@link EventStream} identifier
     * @return The stream identifier
     */
    public String getId() {
        return id;
    }

}
