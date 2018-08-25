package org.eventstore.message;

import org.eventstore.models.Message;

/**
 * Publish notifications about the modifications in a event stream.
 * Can send {@link org.eventstore.models.Message} to all
 * {@link org.eventstore.message.Subscriber}s every time an {@link org.eventstore.models.Event} is added to the
 * {@link org.eventstore.EventStream}
 *
 */
public interface Publisher {
    /**
     * Publish the message to all subscribers
     * @param message The Message to be published
     */
    void publish(Message message);
}
