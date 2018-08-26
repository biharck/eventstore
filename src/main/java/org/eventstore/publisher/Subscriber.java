package org.eventstore.publisher;

import org.eventstore.model.Message;

/**
 * A Handler for {@link Message}s published by {@link Publisher}s affter {@link org.eventstore.model.Event}s are added
 * to {@link org.eventstore.EventStream}
 */
public interface Subscriber {
    /**
     * The function called when a Message is published
     * @param message The published Message
     */
    void on(Message message);
}
