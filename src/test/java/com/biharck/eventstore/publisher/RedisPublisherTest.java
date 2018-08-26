package com.biharck.eventstore.publisher;

import com.biharck.eventstore.EventStore;
import com.biharck.eventstore.EventStoreBuilder;
import com.biharck.eventstore.EventStream;
import com.biharck.eventstore.model.Event;
import com.biharck.eventstore.provider.InMemoryProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RedisPublisherTest {

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
                .setPublisherStrategy(new RedisPublisher("redis://localhost:6379/6"))
                .createEventStore();;
        ordersStream = eventStore.getEventStream(aggregation, streamId);
    }

    @Test
    public void shouldListenToEventsInTheEventStream() throws InterruptedException {

        EventStore eventStoreNotified = new EventStoreBuilder()
                .setProvider(new InMemoryProvider())
                .setPublisherStrategy(new RedisPublisher("redis://localhost:6379/6"))
                .createEventStore();
        count = 0;
        eventStoreNotified.subscribe(ordersStream.getAggregation(), message -> {
            assertThat(message.getAggregation(), is(ordersStream.getAggregation()));
            assertThat(message.getStreamId(), is(ordersStream.getStreamId()));
            assertThat(message.getEvent().getPayload(), is(EVENT_PAYLOAD));
            count++;
        });

        ordersStream.addEvent(new Event(EVENT_PAYLOAD));
        await().atMost(5, TimeUnit.SECONDS).until(() -> count == 1);
    }

    @Test
    public void shouldUnsubscribeToTheEventStream() {
        EventStore eventStoreNotified = new EventStoreBuilder()
                .setProvider(new InMemoryProvider())
                .setPublisherStrategy(new RedisPublisher("redis://localhost:6379/6"))
                .createEventStore();
        count = 0;
        Subscription subscription = eventStoreNotified.subscribe(ordersStream.getAggregation(), message -> {
            count++;
        });

        ordersStream.addEvent(new Event(EVENT_PAYLOAD));

        await().atMost(5, TimeUnit.SECONDS).until(() -> count == 1);
        subscription.remove();

        await().atLeast(1, TimeUnit.SECONDS);
        ordersStream.addEvent(new Event(EVENT_PAYLOAD));
        await().atLeast(1, TimeUnit.SECONDS);
        assertThat(count, is(1));
    }


}
