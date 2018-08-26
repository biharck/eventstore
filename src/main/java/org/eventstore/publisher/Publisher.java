package org.eventstore.publisher;

import org.eventstore.model.Message;

/**
 * Publish notifications about the modifications in a event stream.
 * Can send {@link org.eventstore.model.Message} to all
 * {@link org.eventstore.publisher.Subscriber}s every time an {@link org.eventstore.model.Event} is added to the
 * {@link org.eventstore.EventStream}
 *
 */
public interface Publisher {
    /**
     * Publish the publisher to all subscribers
     * @param message The Message to be published
     */
    void publish(Message message);
}
