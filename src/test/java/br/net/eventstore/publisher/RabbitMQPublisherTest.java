package br.net.eventstore.publisher;

import br.net.eventstore.EventStoreBuilder;
import br.net.eventstore.model.Event;
import br.net.eventstore.provider.InMemoryProvider;
import br.net.eventstore.EventStore;
import br.net.eventstore.EventStream;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.awaitility.Awaitility.await;

public class RabbitMQPublisherTest {

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
                .setPublisher(new RabbitMQPublisher("localhost"))
                .createEventStore();
        ordersStream = eventStore.getEventStream(aggregation, streamId);
    }


    @Test
    public void shouldListenToEventsInTheEventStream() throws InterruptedException {

        EventStore eventStoreNotified = new EventStoreBuilder()
                .setProvider(new InMemoryProvider())
                .setPublisher(new RabbitMQPublisher("localhost"))
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
}
