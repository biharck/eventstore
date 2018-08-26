package io.github.biharck.eventstore.publisher;

import io.github.biharck.eventstore.model.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Publisher that handle all the data in memory. It is a very simple implementation that should be used
 * only for development and test purposes.
 */
public class InMemoryPublisher implements Publisher, HasSubscribers {

    private ConcurrentHashMap<String, List<Subscriber>> listeners = new ConcurrentHashMap<>();

    @Override
    public void publish(Message message) {
        List<Subscriber> aggregateListeners = listeners.get(message.getAggregation());
        if (aggregateListeners != null) {
            aggregateListeners.forEach(subscriber -> subscriber.on(message));
        }
    }

    @Override
    public Subscription subscribe(String aggregation, Subscriber subscriber) {
        List<Subscriber> aggregateListeners = listeners.computeIfAbsent(aggregation,
                key -> Collections.synchronizedList(new ArrayList<>()));
        aggregateListeners.add(subscriber);
        return () -> aggregateListeners.remove(subscriber);
    }
}
