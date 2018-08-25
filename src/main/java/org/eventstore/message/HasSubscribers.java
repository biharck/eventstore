package org.eventstore.message;

public interface HasSubscribers {
    Subscription subscribe(String aggregate, Subscriber subscriber);
}
