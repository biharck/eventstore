package br.net.eventstore.provider;

import br.net.eventstore.EventStore;
import br.net.eventstore.EventStoreBuilder;
import br.net.eventstore.EventStream;
import br.net.eventstore.model.EventPayload;
import br.net.eventstore.publisher.InMemoryPublisher;
import br.net.eventstore.model.Event;
import io.lettuce.core.RedisClient;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class RedisProviderTest {

    private final String EVENT_PAYLOAD = "Event Data";
    private EventStore eventStore;

    public RedisProviderTest() {
        RedisClient.create("redis://localhost:6379/6").connect().sync().flushdb();
    }
    
    @Before
    public void setUp(){
        eventStore = new EventStoreBuilder()
                .setProvider(new RedisProvider("redis://localhost:6379/6"))
                .setPublisher(new InMemoryPublisher())
                .createEventStore();
    }

    @Test
    public void shouldAddAnEventToTheEventStream() throws Exception {
        Event event = getEventStream("orders", "1").addEvent(new EventPayload(EVENT_PAYLOAD));
        assertThat(event, notNullValue());
        assertThat(event.getCommitTimestamp(), notNullValue());
        assertThat(event.getSequence(), notNullValue());
    }

    @Test
    public void shouldGetEventsFromTheEventStream() throws Exception {
        EventStream eventStream = getEventStream("orders", "2");
        eventStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        List<Event> events = eventStream.getEvents().collect(Collectors.toList());
        assertThat(events.size(), is(1));
        assertThat(events.get(0).getPayload(), is(EVENT_PAYLOAD));
        assertThat(events.get(0).getSequence(), is(0l));
    }

    @Test
    public void shouldGetRangedEventsFromTheEventStream() throws Exception {
        EventStream eventStream = getEventStream("orders", "2");
        eventStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        eventStream.addEvent(new EventPayload(EVENT_PAYLOAD + "_1"));
        eventStream.addEvent(new EventPayload(EVENT_PAYLOAD + "_2"));
        List<Event> events = eventStream.getEvents(1,5).collect(Collectors.toList());
        assertThat(events.size(), is(2));
        assertThat(events.get(0).getPayload(), is(EVENT_PAYLOAD + "_1"));
        assertThat(events.get(0).getSequence(), is(1l));
    }

    @Test
    public void shouldGetAggregationsFromTheEventStream() throws Exception {
        EventStream eventStream = getEventStream("orders", "3");
        eventStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        List<String> aggregations = eventStore.getAggregations().collect(Collectors.toList());
        assertThat(aggregations.size(), is(1));
    }

    @Test
    public void shouldGetStreamBasedOnAggregation() throws Exception {
        EventStream eventStream = getEventStream("orders", "4");
        eventStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        List<String> orders = eventStore.getStreams("orders").collect(Collectors.toList());
        assertThat(orders.size(), is(1));
    }

    @Test
    public void shouldGetRangedAggregationsFromTheEventStream() throws Exception {
        EventStream eventStream = getEventStream("orders", "5");
        eventStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        List<String> aggregations = eventStore.getAggregations(0, 1).collect(Collectors.toList());
        assertThat(aggregations.size(), is(1));
    }

    @Test
    public void shouldGetRangedStreamBasedOnAggregation() throws Exception {
        EventStream eventStream = getEventStream("orders", "6");
        eventStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        List<String> orders = eventStore.getStreams("orders", 0, 1).collect(Collectors.toList());
        assertThat(orders.size(), is(1));
    }

    protected EventStream getEventStream(String aggregation, String streamId) {
        return eventStore.getEventStream(aggregation, streamId);
    }

}
