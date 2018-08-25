package org.eventstore;

import org.eventstore.providers.InMemoryProvider;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EventStoreTest {

    private EventStore eventStore;

    @Before
    public void setUp(){
        eventStore = new EventStoreBuilder().setProvider(new InMemoryProvider()).createEventStore();
    }

    @Test
    public void shouldCreateAnEventStreamWhenThereIsNoEventStream(){
        String streamId = "1";
        String aggregate = "orders";
        EventStream ordersStream = eventStore.getEventStream(aggregate, streamId);
        assertThat(ordersStream.getStreamId(), is(streamId));
        assertThat(ordersStream.getAggregation(), is(aggregate));
    }
}
