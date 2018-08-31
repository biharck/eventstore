package br.net.eventstore;

import br.net.eventstore.model.Event;
import br.net.eventstore.provider.InMemoryProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EventStoreTest {

    private EventStore eventStore;

    @Before
    public void setUp(){
        eventStore = new EventStoreBuilder().setProvider(new InMemoryProvider()).createEventStore();
    }

    @Test
    public void shouldCreateAnEventStreamWhenThereIsNoEventStream(){
        String streamId = "1";
        String aggregation = "orders";
        EventStream ordersStream = eventStore.getEventStream(aggregation, streamId);
        assertThat(ordersStream.getStreamId(), is(streamId));
        assertThat(ordersStream.getAggregation(), is(aggregation));
    }

    @Test
    public void shouldBeAbleToGetTheAggregationsList(){
        String streamId = "1";
        String aggregation = "orders";
        EventStream ordersStream = eventStore.getEventStream(aggregation, streamId);
        ordersStream.addEvent(new Event("payload"));
        Stream<String> aggregations = eventStore.getAggregations();
        List<String> aggregationsList = aggregations.collect(Collectors.toList());
        assertThat(aggregationsList.size(), is(1));
        assertThat(aggregationsList.get(0), is(aggregation));
    }
}
