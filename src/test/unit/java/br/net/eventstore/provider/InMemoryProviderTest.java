package br.net.eventstore.provider;

import br.net.eventstore.EventStore;
import br.net.eventstore.EventStoreBuilder;
import br.net.eventstore.EventStream;
import br.net.eventstore.model.EventPayload;
import br.net.eventstore.publisher.InMemoryPublisher;
import br.net.eventstore.model.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryProviderTest {

    private final String EVENT_PAYLOAD = "Event Data";
    private EventStore eventStore;
    private EventStream ordersStream;

    @Before
    public void setUp(){
        String streamId = "1";
        String aggregation = "orders";
        eventStore = new EventStoreBuilder()
                .setProvider(new InMemoryProvider())
                .createEventStore();
        ordersStream = eventStore.getEventStream(aggregation, streamId);
    }

    @Test
    public void shouldAddAnEventToTheEventStream(){
        Event event = ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        assertThat(event, notNullValue());
        assertThat(event.getCommitTimestamp(), notNullValue());
        assertThat(event.getSequence(), notNullValue());
        assertThat(event.getPayload(), is(EVENT_PAYLOAD));
    }

    @Test
    public void shouldGetEventsFromTheEventStream(){
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD + "_1"));
        List<Event> events = ordersStream.getEvents().collect(Collectors.toList());
        assertThat(events.size(), is(2));
        assertThat(events.get(0).getPayload(), is(EVENT_PAYLOAD));
        assertThat(events.get(0).getSequence(), is(0l));
    }

    @Test
    public void shouldGetRangedEventsFromTheEventStream(){
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD + "_1"));
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD + "_2"));
        List<Event> events = ordersStream.getEvents(1,5).collect(Collectors.toList());
        assertThat(events.size(), is(2));
        assertThat(events.get(0).getPayload(), is(EVENT_PAYLOAD + "_1"));
        assertThat(events.get(0).getSequence(), is(1l));
    }

    @Test
    public void shouldGetAggregationsFromTheEventStream(){
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        List<String> aggregations = eventStore.getAggregations().collect(Collectors.toList());
        assertThat(aggregations.size(), is(1));
    }

    @Test
    public void shouldGetStreamBasedOnAggregation(){
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        List<String> orders = eventStore.getStreams("orders").collect(Collectors.toList());
        assertThat(orders.size(), is(1));
        assertThat(orders.get(0), is("1"));
    }

    @Test
    public void shouldGetRangedAggregationsFromTheEventStream(){
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        EventStream offersStream = eventStore.getEventStream("offers", "1");
        offersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        EventStream checkoutStream = eventStore.getEventStream("checkout", "1");
        checkoutStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        EventStream customersStream = eventStore.getEventStream("customers", "1");
        customersStream.addEvent(new EventPayload(EVENT_PAYLOAD));

        List<String> aggregations = eventStore.getAggregations(1, 2).collect(Collectors.toList());
        assertThat(aggregations.size(), is(2));
        assertThat(aggregations.get(0), is("customers"));
        assertThat(aggregations.get(1), is("offers"));
    }

    @Test
    public void shouldGetRangedStreamBasedOnAggregation(){
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        EventStream orders2Stream = eventStore.getEventStream("orders", "2");
        orders2Stream.addEvent(new EventPayload(EVENT_PAYLOAD));
        EventStream orders3Stream = eventStore.getEventStream("orders", "3");
        orders3Stream.addEvent(new EventPayload(EVENT_PAYLOAD));
        EventStream orders4Stream = eventStore.getEventStream("orders", "4");
        orders4Stream.addEvent(new EventPayload(EVENT_PAYLOAD));
        EventStream orders5Stream = eventStore.getEventStream("orders", "5");
        orders5Stream.addEvent(new EventPayload(EVENT_PAYLOAD));
        EventStream orders6Stream = eventStore.getEventStream("orders", "6");
        orders6Stream.addEvent(new EventPayload(EVENT_PAYLOAD));

        List<String> orders = eventStore.getStreams("orders", 2, 3).collect(Collectors.toList());
        assertThat(orders.size(), is(3));
        assertThat(orders.get(0), is("3"));
        assertThat(orders.get(1), is("4"));
        assertThat(orders.get(2), is("5"));
    }

    @Test
    public void shouldHandleEmptyListOfAggregationsInTheEventStream(){
        List<String> aggregations = eventStore.getAggregations().collect(Collectors.toList());
        assertThat(aggregations.size(), is(0));
    }

    @Test
    public void shouldHandleEmptyListOfStreamsInTheEventStream(){
        List<String> orders = eventStore.getStreams("orders").collect(Collectors.toList());
        assertThat(orders.size(), is(0));
    }
}
