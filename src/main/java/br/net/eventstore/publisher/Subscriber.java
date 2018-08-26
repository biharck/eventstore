package br.net.eventstore.publisher;

import br.net.eventstore.EventStream;
import br.net.eventstore.model.Event;
import br.net.eventstore.model.Message;

/**
 * A Handler for {@link Message}s published by {@link Publisher}s affter {@link Event}s are added
 * to {@link EventStream}
 */
public interface Subscriber {
    /**
     * The function called when a Message is published
     * @param message The published Message
     */
    void on(Message message);
}
