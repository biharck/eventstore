package org.eventstore.message;

import org.eventstore.models.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPublisher implements Publisher, HasSubscribers {

    private ConcurrentHashMap<String, List<Subscriber>> listeners = new ConcurrentHashMap<>();

    @Override
    public void publish(Message message) {
        List<Subscriber> aggregateListeners = listeners.get(message.getAggregate());
        if (aggregateListeners != null) {
            aggregateListeners.forEach(subscriber -> subscriber.on(message));
        }
    }

    @Override
    public Subscription subscribe(String aggregate, Subscriber subscriber) {
        List<Subscriber> aggregateListeners = listeners.computeIfAbsent(aggregate,
                key -> Collections.synchronizedList(new ArrayList<>()));
        aggregateListeners.add(subscriber);
        return () -> aggregateListeners.remove(subscriber);
    }
}
