package br.net.eventstore.provider;

import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;
import com.google.gson.Gson;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.List;
import java.util.stream.Stream;

/**
 * A Persistence Provider that handle all the data in redis.
 */
public class RedisProvider implements PersistenceProvider{

    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> commands;
    private Gson serializer;

    public RedisProvider(RedisClient redisClient) {
        connection = redisClient.connect();
        commands = connection.sync();
        serializer = new Gson();
    }

    public RedisProvider(String redisURL) {
        this(RedisClient.create(redisURL));
    }

    @Override
    public Event addEvent(br.net.eventstore.model.Stream stream, EventPayload payload){

        long sequence = commands.incr("sequences:{" + getKey(stream) + "}")-1;
        long timestamp = Long.parseLong(commands.time().get(0));
        Event newEvent = new Event(payload.getData(), timestamp, sequence);

        commands.multi();
        commands.rpush(getKey(stream), serializer.toJson(newEvent));
        commands.zadd("meta:aggregations", 1,stream.getAggregation());
        commands.zadd("meta:aggregations:"+stream.getAggregation(), 1, stream.getId());
        commands.exec();

        return newEvent;
    }

    @Override
    public Stream<Event> getEvents(br.net.eventstore.model.Stream stream){
        return getEvents(stream, 0, -1);
    }

    @Override
    public Stream<Event> getEvents(br.net.eventstore.model.Stream stream, int offset, int limit) {
        List<String> history = commands.lrange(getKey(stream), offset, limit);
        return history.stream().map(data -> serializer.fromJson(data, Event.class));
    }

    @Override
    public Stream<String> getAggregations() {
        return getAggregations(0, -1);
    }

    @Override
    public Stream<String> getAggregations(int offset, int limit) {
        return commands.zrange("meta:aggregations", offset, limit).stream();
    }

    @Override
    public Stream<String> getStreams(String aggregation) {
        return getStreams(aggregation, 0, -1);
    }

    @Override
    public Stream<String> getStreams(String aggregation, int offset, int limit) {
        return commands.zrange("meta:aggregations:"+aggregation, offset, limit).stream();
    }

    private String getKey(br.net.eventstore.model.Stream stream) {

        return stream.getAggregation() + ":" + stream.getId();
    }

}