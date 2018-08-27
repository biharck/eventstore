package br.net.eventstore.provider;

import br.net.eventstore.EventStore;
import br.net.eventstore.EventStoreBuilder;
import br.net.eventstore.EventStream;
import br.net.eventstore.publisher.InMemoryPublisher;
import br.net.eventstore.model.Event;
import io.lettuce.core.RedisClient;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
    public void shouldAddAnEventToTheEventStream(){
        Event event = getEventStream("orders", "1").addEvent(new Event(EVENT_PAYLOAD));
        assertThat(event, notNullValue());
        assertThat(event.getCommitTimestamp(), notNullValue());
        assertThat(event.getSequence(), notNullValue());
    }

    @Test
    public void shouldGetEventsFromTheEventStream(){
        EventStream eventStream = getEventStream("orders", "2");
        eventStream.addEvent(new Event(EVENT_PAYLOAD));
        List<Event> events = eventStream.getEvents();
        assertThat(events.size(), is(1));
        assertThat(events.get(0).getPayload(), is(EVENT_PAYLOAD));
        assertThat(events.get(0).getSequence(), is(0l));
    }

    protected EventStream getEventStream(String aggregation, String streamId) {
        return eventStore.getEventStream(aggregation, streamId);
    }

}
