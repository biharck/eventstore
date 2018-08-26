package io.github.biharck.eventstore;

import io.github.biharck.eventstore.provider.InMemoryProvider;
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
        String aggregation = "orders";
        EventStream ordersStream = eventStore.getEventStream(aggregation, streamId);
        assertThat(ordersStream.getStreamId(), is(streamId));
        assertThat(ordersStream.getAggregation(), is(aggregation));
    }
}
