package br.net.eventstore.publisher;

import br.net.eventstore.EventStore;
import br.net.eventstore.EventStoreBuilder;
import br.net.eventstore.EventStream;
import br.net.eventstore.model.Event;
import br.net.eventstore.provider.InMemoryProvider;
import io.lettuce.core.RedisClient;
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

    public RedisPublisherTest() {
        RedisClient.create("redis://localhost:6379/6").connect().sync().flushdb();
    }


    @Before
    public void setUp(){
        String streamId = "1";
        String aggregation = "orders";
        eventStore = new EventStoreBuilder()
                .setProvider(new InMemoryProvider())
                .setPublisher(new RedisPublisher("redis://localhost:6379/6"))
                .createEventStore();
        ordersStream = eventStore.getEventStream(aggregation, streamId);
    }

    @Test
    public void shouldListenToEventsInTheEventStream() throws InterruptedException {

        EventStore eventStoreNotified = new EventStoreBuilder()
                .setProvider(new InMemoryProvider())
                .setPublisher(new RedisPublisher("redis://localhost:6379/6"))
                .createEventStore();
        count = 0;
        eventStoreNotified.subscribe(ordersStream.getAggregation(), message -> {
            assertThat(message.getAggregation(), is(ordersStream.getAggregation()));
            assertThat(message.getStreamId(), is(ordersStream.getStreamId()));
            assertThat(message.getEvent().getPayload(), is(EVENT_PAYLOAD));
            count++;
        });

        ordersStream.addEvent(new Event(EVENT_PAYLOAD));
        await().atMost(10, TimeUnit.SECONDS).until(() -> count == 1);
    }

    @Test
    public void shouldUnsubscribeToTheEventStream() {
        EventStore eventStoreNotified = new EventStoreBuilder()
                .setProvider(new InMemoryProvider())
                .setPublisher(new RedisPublisher("redis://localhost:6379/6"))
                .createEventStore();
        count = 0;
        Subscription subscription = eventStoreNotified.subscribe(ordersStream.getAggregation(), message -> {
            count++;
        });

        ordersStream.addEvent(new Event(EVENT_PAYLOAD));

        await().atMost(10, TimeUnit.SECONDS).until(() -> count == 1);
        subscription.remove();

        await().atLeast(2, TimeUnit.SECONDS);
        ordersStream.addEvent(new Event(EVENT_PAYLOAD));
        await().atLeast(2, TimeUnit.SECONDS);
        assertThat(count, is(1));
    }


}
