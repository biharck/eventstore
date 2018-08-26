package br.net.eventstore.publisher;

import br.net.eventstore.EventStore;

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
