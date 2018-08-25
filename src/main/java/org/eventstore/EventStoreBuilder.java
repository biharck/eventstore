package org.eventstore;

import org.eventstore.message.Publisher;
import org.eventstore.providers.Provider;

public class EventStoreBuilder {
    private Provider provider;
    private Publisher publisher;

    public EventStoreBuilder setProvider(Provider provider) {
        this.provider = provider;
        return this;
    }

    public EventStoreBuilder setPublisher(Publisher publisher) {
        this.publisher = publisher;
        return this;
    }

    public EventStore createEventStore() {
        return new EventStore(provider, publisher);
    }
}