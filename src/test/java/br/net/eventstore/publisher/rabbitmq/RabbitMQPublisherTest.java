package br.net.eventstore.publisher.rabbitmq;

import br.net.eventstore.EventStore;
import br.net.eventstore.EventStoreBuilder;
import br.net.eventstore.EventStream;
import br.net.eventstore.model.EventPayload;
import br.net.eventstore.provider.InMemoryProvider;
import br.net.eventstore.publisher.PublishException;
import br.net.eventstore.publisher.SubscriptionException;
import com.rabbitmq.client.Channel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RabbitMQPublisherTest {

    private final String EVENT_PAYLOAD = "Event Data";
    private EventStore eventStore;
    private EventStream ordersStream;
    @Mock private ChannelPool pool;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

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

    @Test(expected = PublishException.class)
    public void shouldHandleExceptionsWhenPublishing() throws Exception {
        when(pool.borrowObject()).thenThrow(new Exception("Testing PublishException"));

        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        verify(pool, times(0)).returnObject(any(Channel.class));
    }

    @Test(expected = SubscriptionException.class)
    public void shouldHandleExceptionsWhenSubscription() throws Exception {
        when(pool.borrowObject()).thenThrow(new Exception("Testing SubscriptionException"));

        eventStore.subscribe(ordersStream.getAggregation(), message -> {
            fail("Should not be called");
        });
        verify(pool, times(0)).returnObject(any(Channel.class));
    }

}
