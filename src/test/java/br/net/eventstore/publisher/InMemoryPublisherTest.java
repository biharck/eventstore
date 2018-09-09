package br.net.eventstore.publisher;

import br.net.eventstore.EventStore;
import br.net.eventstore.EventStoreBuilder;
import br.net.eventstore.EventStream;
import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;
import br.net.eventstore.provider.InMemoryProvider;
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
    public void shouldListenToEventsInTheEventStream() throws Exception {

        eventStore.subscribe(ordersStream.getAggregation(), message -> {
            assertThat(message.getStream().getAggregation(), is(ordersStream.getAggregation()));
            assertThat(message.getStream().getId(), is(ordersStream.getStreamId()));
            assertThat(message.getEvent().getPayload(), is(EVENT_PAYLOAD));
        });

        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
    }

    @Test
    public void shouldUnsubscribeToTheEventStream() throws Exception {
        count = 0;
        Subscription subscription = eventStore.subscribe(ordersStream.getAggregation(), message -> {
            count++;
        });

        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        assertThat(count, is(1));
        subscription.remove();
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        assertThat(count, is(1));
    }
}
