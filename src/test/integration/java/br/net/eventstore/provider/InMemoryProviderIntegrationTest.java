package br.net.eventstore.provider;

import br.net.eventstore.EventStore;
import br.net.eventstore.EventStoreBuilder;
import br.net.eventstore.EventStream;
import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;
import br.net.eventstore.publisher.InMemoryPublisher;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class InMemoryProviderIntegrationTest {

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
    public void shouldGetRangedAggregationsFromTheEventStream(){
        ordersStream.addEvent(new EventPayload(EVENT_PAYLOAD));
        List<String> aggregations = eventStore.getAggregations(0, 1).collect(Collectors.toList());
        assertThat(aggregations.size(), is(1));
    }

}
