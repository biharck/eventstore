package br.net.eventstore.provider;

import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedisProviderTest {

    private RedisProvider redisProvider;

    @Mock private RedisClient redisClient;

    @Mock private StatefulRedisConnection<String, String> connection;

    @Mock private RedisCommands<String, String> commands;


    @Before
    public void setUp(){
//        redisClient = mock(RedisClient.class);
//        connection = mock(StatefulRedisConnection.class);
//        commands = mock(RedisCommands.class);
        when(redisClient.connect()).thenReturn(connection);
        when(connection.sync()).thenReturn(commands);
        redisProvider = new RedisProvider(redisClient);
    }

    @Test
    public void shouldGetARangeOfEvents(){
        when(commands.lrange(anyString(), anyLong(), anyLong()))
                .thenReturn(Collections.singletonList("{ \"payload\": {\"data\": \"EVENT PAYLOAD\"} }"));

        Stream<Event> events = redisProvider.getEvents(
                new br.net.eventstore.model.Stream("orders", "1"), 2, 5);

        List<Event> result = events.collect(Collectors.toList());

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getPayload().getData(), is("EVENT PAYLOAD"));
        verify(commands).lrange("orders:1", 2, 5);
    }

    @Test
    public void shouldGetEvents(){
        when(commands.lrange(anyString(), anyLong(), anyLong()))
                .thenReturn(Collections.singletonList("{ \"payload\": {\"data\": \"EVENT PAYLOAD\"} }"));

        Stream<Event> events = redisProvider.getEvents(
                new br.net.eventstore.model.Stream("orders", "1"));

        List<Event> result = events.collect(Collectors.toList());

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getPayload().getData(), is("EVENT PAYLOAD"));
        verify(commands).lrange("orders:1", 0, -1);
    }

    @Test
    public void shouldGetARangeOfAggregations(){
        when(commands.zrange(anyString(), anyLong(), anyLong()))
                .thenReturn(Collections.singletonList("orders"));

        Stream<String> aggregations = redisProvider.getAggregations(2, 5);

        List<String> result = aggregations.collect(Collectors.toList());

        assertThat(result.size(), is(1));
        assertThat(result.get(0), is("orders"));
        verify(commands).zrange("meta:aggregations", 2, 5);
    }

    @Test
    public void shouldGetAggregations(){
        when(commands.zrange(anyString(), anyLong(), anyLong()))
                .thenReturn(Arrays.asList("orders", "offers", "checkout", "customers"));

        Stream<String> aggregations = redisProvider.getAggregations();

        List<String> result = aggregations.collect(Collectors.toList());

        assertThat(result.size(), is(4));
        assertThat(result.get(0), is("orders"));
        verify(commands).zrange("meta:aggregations", 0, -1);
    }

    @Test
    public void shouldGetARangeOfStreams(){
        when(commands.zrange(anyString(), anyLong(), anyLong()))
                .thenReturn(Collections.singletonList("1"));

        Stream<String> aggregations = redisProvider.getStreams("orders", 2, 5);

        List<String> result = aggregations.collect(Collectors.toList());

        assertThat(result.size(), is(1));
        assertThat(result.get(0), is("1"));
        verify(commands).zrange("meta:aggregations:orders", 2, 5);
    }

    @Test
    public void shouldGetStreams(){
        when(commands.zrange(anyString(), anyLong(), anyLong()))
                .thenReturn(Arrays.asList("1", "2", "3", "4"));

        Stream<String> aggregations = redisProvider.getStreams("orders");

        List<String> result = aggregations.collect(Collectors.toList());

        assertThat(result.size(), is(4));
        assertThat(result.get(0), is("1"));
        verify(commands).zrange("meta:aggregations:orders", 0, -1);
    }

    @Test
    public void shouldAddEventToStream(){
        when(commands.incr(anyString())).thenReturn(1l);
        when(commands.time()).thenReturn(Collections.singletonList("1"));

        Event event = redisProvider.addEvent(
                new br.net.eventstore.model.Stream("orders", "1"),
                new EventPayload("EVENT PAYLOAD"));

        assertThat(event.getSequence(), is(0l));
        assertThat(event.getCommitTimestamp(), is(1l));
        verify(commands).incr("sequences:{orders:1}");
        verify(commands).time();
        verify(commands).multi();
        verify(commands).rpush("orders:1", "{\"payload\":{\"data\":\"EVENT PAYLOAD\"},\"commitTimestamp\":1,\"sequence\":0}");
        verify(commands).zadd("meta:aggregations", 1.0, "orders");
        verify(commands).zadd("meta:aggregations:orders", 1.0, "1");
        verify(commands).exec();
    }
}
