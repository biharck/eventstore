package org.eventstore;

import org.eventstore.message.InMemoryPublisher;
import org.eventstore.message.Subscription;
import org.eventstore.models.Event;
import org.eventstore.providers.InMemoryProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class EventStreamTest {

    private final String EVENT_PAYLOAD = "Event Data";
    private EventStore eventStore;
    private EventStream ordersStream;
    private int count = 0;

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
    public void shouldAddAnEventToTheEventStream(){
        Event event = ordersStream.addEvent(new Event(EVENT_PAYLOAD));
        assertThat(event, notNullValue());
        assertThat(event.getCommitTimestamp(), notNullValue());
        assertThat(event.getSequence(), notNullValue());
    }

    @Test
    public void shouldGetEventsFromTheEventStream(){
        ordersStream.addEvent(new Event(EVENT_PAYLOAD));
        List<Event> events = ordersStream.getEvents();
        assertThat(events.size(), is(1));
        assertThat(events.get(0).getPayload(), is(EVENT_PAYLOAD));
        assertThat(events.get(0).getSequence(), is(0l));
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
