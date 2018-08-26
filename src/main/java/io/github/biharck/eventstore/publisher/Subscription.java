package io.github.biharck.eventstore.publisher;

import io.github.biharck.eventstore.EventStore;

/**
 * A subscription in the {@link EventStore} notification channel.
 * Can be used to remove the subscription to the publisher channel
 */
public interface Subscription {
    /**
     * Remove the subscription
     */
    void remove();
}
