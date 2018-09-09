package br.net.eventstore.publisher.rabbitmq;

import br.net.eventstore.EventStore;
import br.net.eventstore.EventStoreBuilder;
import br.net.eventstore.EventStream;
import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;
import br.net.eventstore.provider.InMemoryProvider;
import br.net.eventstore.publisher.Subscription;
import com.rabbitmq.client.Channel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RabbitMQPublisherIntegrationTest {

    protected final String EVENT_PAYLOAD = "Event Data";
    protected EventStore eventStore;
    protected EventStream ordersStream;
    protected int count = 0;
    @Spy private ChannelPool pool = new ChannelPool("amqp://localhost");

    @Before
    public void setUp(){
        String streamId = "1";
        String aggregation = "orders";
        eventStore = new EventStoreBuilder()
                .setProvider(new InMemoryProvider())
                .setPublisher(new RabbitMQPublisher(pool))
                .createEventStore();
        ordersStream = eventStore.getEventStream(aggregation, streamId);
    }

    @Test
    public void shouldListenToEventsInTheEventStream() throws Exception {
        count = 0;
        eventStore.subscribe(ordersStream.getAggregation(), message -> {
            assertThat(message.getStream().getId(), is(ordersStream.getStreamId()));
            assertThat(message.getStream().getAggregation(), is(ordersStream.getAggregation()));
            assertThat(message.getEvent().getPayload(), is(EVENT_PAYLOAD));
            count++;
        });

        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        await().atMost(2, TimeUnit.SECONDS).until(() -> count == 1);
        verify(pool, times(2)).returnObject(any(Channel.class));
    }

    @Test
    public void shouldUnsubscribeToTheEventStream() throws Exception {
        count = 0;
        Subscription subscription = eventStore.subscribe(ordersStream.getAggregation(), message -> {
            count++;
        });

        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));

        await().atMost(2, TimeUnit.SECONDS).until(() -> count == 1);
       subscription.remove();

       verify(pool, times(3)).returnObject(any(Channel.class));

        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));

        Thread.sleep(1000);

        assertThat(count, is(1));
    }
}
