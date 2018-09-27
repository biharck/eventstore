package br.net.eventstore.publisher;

import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;
import br.net.eventstore.model.Message;
import br.net.eventstore.model.Stream;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedisPublisherTest {

    private final String EVENT_PAYLOAD = "Event Data";
    private RedisPublisher redisPublisher;

    @Mock
    private RedisClient redisClient;

    @Mock private StatefulRedisPubSubConnection<String, String> connection;

    @Mock private RedisPubSubAsyncCommands<String, String> commands;


    @Before
    public void setUp(){
        when(redisClient.connectPubSub()).thenReturn(connection);
        when(connection.async()).thenReturn(commands);
        redisPublisher = new RedisPublisher(redisClient);
    }

    @Test
    public void shouldPublishMessagesToRedis(){
        Message message = new Message()
                .setStream(new Stream("orders", "1"))
                .setEvent(new Event(new EventPayload(EVENT_PAYLOAD), 123, 2));

        redisPublisher.publish(message);

        verify(commands).publish(message.getStream().getAggregation(),
                "{\"stream\":{\"aggregation\":\"orders\",\"id\":\"1\"},\"event\":{\"payload\":\""
                        +EVENT_PAYLOAD+"\",\"commitTimestamp\":123,\"sequence\":2}}");
    }

    @Test
    public void shouldSubscribeToChangesInEventStore(){
        Message message = new Message()
                .setStream(new Stream("orders", "1"))
                .setEvent(new Event(new EventPayload(EVENT_PAYLOAD), 123, 2));


        Subscriber subscriberOrdersStub = mock(Subscriber.class);
        Subscriber subscriberOrders2Stub = mock(Subscriber.class);
        Subscription subscription = redisPublisher.subscribe("orders", subscriberOrdersStub);
        redisPublisher.subscribe("orders", subscriberOrders2Stub);

        subscription.remove();

        verify(commands, times(1)).subscribe("orders");
        verify(connection, times(1)).addListener(Mockito.any(RedisPubSubListener.class));
        verify(commands, never()).unsubscribe("orders");
    }

    @Test
    public void shouldUnsubscribeToChangesInEventStore(){
        Message message = new Message()
                .setStream(new Stream("orders", "1"))
                .setEvent(new Event(new EventPayload(EVENT_PAYLOAD), 123, 2));


        Subscriber subscriberOrdersStub = mock(Subscriber.class);
        Subscription subscription = redisPublisher.subscribe("orders", subscriberOrdersStub);

        subscription.remove();

        verify(commands, times(1)).subscribe("orders");
        verify(connection, times(1)).addListener(Mockito.any(RedisPubSubListener.class));
        verify(commands, times(1)).unsubscribe("orders");
    }

}
