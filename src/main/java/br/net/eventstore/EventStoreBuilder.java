package br.net.eventstore;

import br.net.eventstore.model.Event;
import br.net.eventstore.model.Message;
import br.net.eventstore.publisher.Publisher;
import br.net.eventstore.publisher.Subscriber;
import br.net.eventstore.provider.Provider;

/**
 * Builder class for {@link EventStore} instances
 */
public class EventStoreBuilder {
    private Provider provider;
    private Publisher publisher;

    /**
     * Inform the {@link Provider} used to handle the persistence of the events in the EventStore
     * @param provider Handle the event persistence
     * @return The Builder instance
     */
    public EventStoreBuilder setProvider(Provider provider) {
        this.provider = provider;
        return this;
    }

    /**
     * Inform a {@link Publisher} to be used to publish notifications about the modifications in a event stream.
     * If a Publisher is configured, a {@link Message} will be sent to all
     * {@link Subscriber}s every time an {@link Event} is added to the
     * {@link EventStream}
     * @param publisher Handle the Message notifications
     * @return The Builder instance
     */
    public EventStoreBuilder setPublisherStrategy(Publisher publisher) {
        this.publisher = publisher;
        return this;
    }

    /**
     * Create a new {@link EventStore} instance
     * @return The {@link EventStore} instance
     */
    public EventStore createEventStore() {
        return new EventStore(provider, publisher);
    }
}