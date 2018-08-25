package org.eventstore.message;

/**
 * A subscription in the {@link org.eventstore.EventStore} notification channel.
 * Can be used to remove the subscription to the message channel
 */
public interface Subscription {
    /**
     * Remove the subscription
     */
    void remove();
}
