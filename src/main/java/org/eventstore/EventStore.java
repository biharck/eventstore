package org.eventstore;

import org.eventstore.message.HasSubscribers;
import org.eventstore.message.Subscriber;
import org.eventstore.message.Subscription;
import org.eventstore.providers.Provider;
import org.eventstore.message.Publisher;

public class EventStore implements HasSubscribers {

    private Provider provider;
    private Publisher publisher;

    EventStore(Provider provider, Publisher publisher){
        this.provider = provider;
        this.publisher = publisher;
    }

    public EventStream getEventStream(String aggregation, String streamId){
        return new EventStream(this, aggregation, streamId);
    }

    @Override
    public Subscription subscribe(String aggregate, Subscriber subscriber) {
        assert (publisher != null && publisher instanceof  HasSubscribers):
                "There is no valid Publisher configured. Configure a Publisher that implements HasSubscribers interface";
        return ((HasSubscribers) publisher).subscribe(aggregate, subscriber);
    }

    Provider getProvider() {
        return provider;
    }

    Publisher getPublisher() {
        return publisher;
    }
}
