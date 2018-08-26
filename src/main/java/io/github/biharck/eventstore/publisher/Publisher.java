package io.github.biharck.eventstore.publisher;

import io.github.biharck.eventstore.EventStream;
import io.github.biharck.eventstore.model.Event;
import io.github.biharck.eventstore.model.Message;

/**
 * Publish notifications about the modifications in a event stream.
 * Can send {@link Message} to all
 * {@link Subscriber}s every time an {@link Event} is added to the
 * {@link EventStream}
 *
 */
public interface Publisher {
    /**
     * Publish the publisher to all subscribers
     * @param message The Message to be published
     */
    void publish(Message message);
}
