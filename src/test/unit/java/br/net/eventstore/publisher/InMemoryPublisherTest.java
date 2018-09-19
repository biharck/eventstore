package br.net.eventstore.publisher;

import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;
import br.net.eventstore.model.Message;
import br.net.eventstore.model.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryPublisherTest {

    private final String EVENT_PAYLOAD = "Event Data";
    private InMemoryPublisher memoryPublisher;

    @Before
    public void setUp(){
        memoryPublisher = new InMemoryPublisher();
    }

    @Test
    public void shouldPublishMessagesToListeners(){
        Message message = new Message()
                .setStream(new Stream("orders", "1"))
                .setEvent(new Event(new EventPayload(EVENT_PAYLOAD), 123, 2));

        Subscriber subscriberStub = Mockito.mock(Subscriber.class);
        memoryPublisher.subscribe("orders", subscriberStub);
        memoryPublisher.publish(message);

        verify(subscriberStub).on(message);
    }

    @Test
    public void shouldNotifyMultipleListeners(){
        Message message = new Message()
                .setStream(new Stream("orders", "1"))
                .setEvent(new Event(new EventPayload(EVENT_PAYLOAD), 123, 2));

        Subscriber subscriberStub = Mockito.mock(Subscriber.class);
        Subscriber subscriber2Stub = Mockito.mock(Subscriber.class);

        memoryPublisher.subscribe("orders", subscriberStub);
        memoryPublisher.subscribe("orders", subscriber2Stub);

        memoryPublisher.publish(message);

        verify(subscriberStub).on(message);
        verify(subscriber2Stub).on(message);
    }

    @Test
    public void shouldNotifyOnlyTheRightListeners(){
        Message message = new Message()
                .setStream(new Stream("offers", "1"))
                .setEvent(new Event(new EventPayload(EVENT_PAYLOAD), 123, 2));

        Subscriber subscriberStub = Mockito.mock(Subscriber.class);
        memoryPublisher.subscribe("orders", subscriberStub);

        memoryPublisher.publish(message); // status
        verify(subscriberStub, never()).on(message);
    }

    @Test
    public void shouldUnsubscribeListener(){
        Message message = new Message()
                .setStream(new Stream("orders", "1"))
                .setEvent(new Event(new EventPayload(EVENT_PAYLOAD), 123, 2));

        Subscriber subscriberStub = Mockito.mock(Subscriber.class);
        Subscription subscription = memoryPublisher.subscribe("orders", subscriberStub);

        subscription.remove();

        memoryPublisher.publish(message); // status
        verify(subscriberStub, never()).on(message);
    }

}
