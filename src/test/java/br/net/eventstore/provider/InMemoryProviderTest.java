package br.net.eventstore.provider;

import br.net.eventstore.EventStore;
import br.net.eventstore.EventStoreBuilder;
import br.net.eventstore.EventStream;
import br.net.eventstore.model.EventPayload;
import br.net.eventstore.publisher.InMemoryPublisher;
import br.net.eventstore.model.Event;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class InMemoryProviderTest {

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
                .createEventStore();
        ordersStream = eventStore.getEventStream(aggregation, streamId);
    }

    @Test
    public void shouldAddAnEventToTheEventStream() throws Exception {
        Event event = ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        assertThat(event, notNullValue());
        assertThat(event.getCommitTimestamp(), notNullValue());
        assertThat(event.getSequence(), notNullValue());
        assertThat(event.getPayload(), is(EVENT_PAYLOAD));
    }

    @Test
    public void shouldGetEventsFromTheEventStream() throws Exception {
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        List<Event> events = ordersStream.getEvents().collect(Collectors.toList());
        assertThat(events.size(), is(1));
        assertThat(events.get(0).getPayload(), is(EVENT_PAYLOAD));
        assertThat(events.get(0).getSequence(), is(0l));
    }

    @Test
    public void shouldGetRangedEventsFromTheEventStream() throws Exception {
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD + "_1"));
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD + "_2"));
        List<Event> events = ordersStream.getEvents(1,5).collect(Collectors.toList());
        assertThat(events.size(), is(2));
        assertThat(events.get(0).getPayload(), is(EVENT_PAYLOAD + "_1"));
        assertThat(events.get(0).getSequence(), is(1l));
    }

    @Test
    public void shouldGetRangedAggregationsFromTheEventStream() throws Exception {
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        List<String> aggregations = eventStore.getAggregations(0, 1).collect(Collectors.toList());
        assertThat(aggregations.size(), is(1));
    }

    @Test
    public void shouldGetRangedStreamBasedOnAggregation() throws Exception {
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        List<String> orders = eventStore.getStreams("orders", 0, 1).collect(Collectors.toList());
        assertThat(orders.size(), is(1));
        assertThat(orders.get(0), is("1"));
    }

}
