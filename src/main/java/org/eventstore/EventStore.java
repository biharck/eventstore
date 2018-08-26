package org.eventstore;

import org.eventstore.publisher.HasSubscribers;
import org.eventstore.publisher.Subscriber;
import org.eventstore.publisher.Subscription;
import org.eventstore.provider.Provider;
import org.eventstore.publisher.Publisher;

/**
 * The EventStore itself. To create EventStore instances, use the {@link EventStoreBuilder}
 */
public class EventStore implements HasSubscribers {

    private Provider provider;
    private Publisher publisher;

    EventStore(Provider provider, Publisher publisher){
        this.provider = provider;
        this.publisher = publisher;
    }

    /**
     * Retrieve an event stream.
     * @param aggregation The parent aggregation for the event stream
     * @param streamId The stream identifier. Can be any string
     * @return The existing stream. If no stream exists for to the given id, a new one
     * will be created when the first event is added to the stream.
     */
    public EventStream getEventStream(String aggregation, String streamId){
        return new EventStream(this, aggregation, streamId);
    }

    /**
     * Add a new subscription to notifications channel associated with the given aggregation.
     * It is necessary to have a valid {@link Publisher} configured that supports subscriptions.
     * @param aggregation The aggregation for the stream events
     * @param subscriber Declares the function to be called to handle new messages
     * @return A subscription. Can be used to remove the subscription to the publisher channel.
     */
    @Override
    public Subscription subscribe(String aggregation, Subscriber subscriber) {
        assert (publisher != null && publisher instanceof  HasSubscribers):
                "There is no valid Publisher configured. Configure a Publisher that implements HasSubscribers interface";
        return ((HasSubscribers) publisher).subscribe(aggregation, subscriber);
    }

    Provider getProvider() {
        return provider;
    }

    Publisher getPublisher() {
        return publisher;
    }
}
