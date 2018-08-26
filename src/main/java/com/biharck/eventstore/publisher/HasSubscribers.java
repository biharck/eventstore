package com.biharck.eventstore.publisher;

/**
 * A class that can receive subscriptions for a notification channel
 */
public interface HasSubscribers {
    /**
     * Add a new subscription to notifications channel associated with the given aggregation.
     * @param aggregation The aggregation for the stream events
     * @param subscriber Declares the function to be called to handle new messages
     * @return A subscription. Can be used to remove the subscription to the publisher channel.
     */
    Subscription subscribe(String aggregation, Subscriber subscriber);
}
