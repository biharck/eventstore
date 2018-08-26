package com.biharck.eventstore.publisher;

import com.biharck.eventstore.EventStore;
import com.biharck.eventstore.EventStoreBuilder;
import com.biharck.eventstore.EventStream;
import com.biharck.eventstore.model.Event;
import com.biharck.eventstore.provider.InMemoryProvider;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class InMemoryPublisherTest {

    protected final String EVENT_PAYLOAD = "Event Data";
    protected EventStore eventStore;
    protected EventStream ordersStream;
    protected int count = 0;

    @Before
    public void setUp(){
        String streamId = "1";
        String aggregation = "orders";
        eventStore = new EventStoreBuilder()
                .setProvider(new InMemoryProvider())
                .setPublisher(new InMemoryPublisher())
                .createEventStore();;
        ordersStream = eventStore.getEventStream(aggregation, streamId);
    }

    @Test
    public void shouldListenToEventsInTheEventStream() {

        eventStore.subscribe(ordersStream.getAggregation(), message -> {
            assertThat(message.getAggregation(), is(ordersStream.getAggregation()));
            assertThat(message.getStreamId(), is(ordersStream.getStreamId()));
            assertThat(message.getEvent().getPayload(), is(EVENT_PAYLOAD));
        });

        ordersStream.addEvent(new Event(EVENT_PAYLOAD));
    }

    @Test
    public void shouldUnsubscribeToTheEventStream() {
        count = 0;
        Subscription subscription = eventStore.subscribe(ordersStream.getAggregation(), message -> {
            count++;
        });

        ordersStream.addEvent(new Event(EVENT_PAYLOAD));
        assertThat(count, is(1));
        subscription.remove();;
        ordersStream.addEvent(new Event(EVENT_PAYLOAD));
        assertThat(count, is(1));
    }
}
