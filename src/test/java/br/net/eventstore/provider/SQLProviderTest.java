package br.net.eventstore.provider;

import br.net.eventstore.EventStore;
import br.net.eventstore.EventStoreBuilder;
import br.net.eventstore.EventStream;
import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;
import br.net.eventstore.provider.sql.SQLProvider;
import br.net.eventstore.publisher.InMemoryPublisher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SQLProviderTest {

    private EventStore eventStore;
    private static final String EVENT_PAYLOAD = "payload";

    @Before
    public void setUp(){
        eventStore = new EventStoreBuilder()
                .setProvider(new SQLProvider())
                .setPublisher(new InMemoryPublisher())
                .createEventStore();
    }

    protected EventStream getEventStream(String aggregation, String streamId) {
        return eventStore.getEventStream(aggregation, streamId);
    }

    @Test
    public void shouldAddAnEventToTheEventStream() {

        Event event = getEventStream("orders", "1").addEvent(new EventPayload(EVENT_PAYLOAD));
        assertThat(event, notNullValue());
        assertThat(event.getCommitTimestamp(), notNullValue());
        assertThat(event.getSequence(), is(1l));
    }

}
