package br.net.eventstore;

import br.net.eventstore.provider.PersistenceProvider;
import br.net.eventstore.publisher.*;

import java.util.stream.Stream;

/**
 * The EventStore itself. To create EventStore instances, use the {@link EventStoreBuilder}
 */
public class EventStore implements HasSubscribers {

    private PersistenceProvider provider;
    private Publisher publisher;

    EventStore(PersistenceProvider provider, Publisher publisher){
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
        return new EventStream(this, new br.net.eventstore.model.Stream(aggregation, streamId));
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
        assert (publisher instanceof  HasSubscribers):
                "There is no valid Publisher configured. Configure a Publisher that implements HasSubscribers interface";
        return ((HasSubscribers) publisher).subscribe(aggregation, subscriber);
    }

    /**
     * Retrieves the aggregation list
     * @return The aggregation list
     */
    public Stream<String> getAggregations(){
        return getProvider().getAggregations();
    }

    /**
     * Retrieves a ranged aggregation list
     * @param offset The start position in the aggregation list
     * @param limit The desired quantity aggregations
     * @return The aggregation list
     */
    public Stream<String> getAggregations(int offset, int limit){
        return getProvider().getAggregations(offset, limit);
    }

    /**
     * Retrieves the stream list
     * @param aggregation The aggregation
     * @return The stream list
     */
    public Stream<String> getStreams(String aggregation){
        return getProvider().getStreams(aggregation);
    }

    /**
     * Retrieves a ranged stream list
     * @param aggregation The aggregation
     * @param offset The start position in the stream list
     * @param limit The desired quantity streams
     * @return The stream list
     */
    public Stream<String> getStreams(String aggregation, int offset, int limit){
        return getProvider().getStreams(aggregation, offset, limit);
    }

    PersistenceProvider getProvider() {
        return provider;
    }

    Publisher getPublisher() {
        return publisher;
    }
}
