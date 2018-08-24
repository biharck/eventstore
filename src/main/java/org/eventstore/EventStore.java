package org.eventstore;

import org.eventstore.providers.Provider;

public class EventStore {

    private final Provider provider;

    public EventStore(Provider provider){
        this.provider = provider;
    }

    public EventStream getEventStream(String aggregation, String streamId){
        return new EventStream(this.provider, aggregation, streamId);
    }

}
