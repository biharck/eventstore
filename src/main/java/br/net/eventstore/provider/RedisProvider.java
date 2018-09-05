package br.net.eventstore.provider;

import br.net.eventstore.model.Event;
import br.net.eventstore.model.EventPayload;
import com.google.gson.Gson;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

/**
 * A Persistence Provider that handle all the data in redis.
 */
public class RedisProvider implements PersistenceProvider{

    private StatefulRedisPubSubConnection<String, String> connection;
    private RedisCommands<String, String> commands;
    private Gson serializer;

    public RedisProvider(RedisClient redisClient) {
        connection = redisClient.connectPubSub();
        commands = connection.sync();
        serializer = new Gson();
    }

    public RedisProvider(String redisURL) {
        this(RedisClient.create(redisURL));
    }

    @Override
    public Event addEvent(String aggregation, String streamId, EventPayload payload){

        long sequence = commands.incr("sequences:{" + getKey(aggregation, streamId) + "}")-1;
        long timestamp = Long.parseLong(commands.time().get(0));
        Event newEvent = new Event(payload.getData(), timestamp, sequence);

        commands.multi();
        commands.rpush(getKey(aggregation, streamId), serializer.toJson(newEvent));
        commands.zadd("meta:aggregations:"+aggregation, 1, streamId);
        commands.zadd("meta:aggregations", 1, aggregation);
        commands.exec();

        return newEvent;
    }

    @Override
    public Stream<Event> getEvents(String aggregation, String streamId){
        return getEvents(aggregation, streamId, 0, -1);
    }

    @Override
    public Stream<Event> getEvents(String aggregation, String streamId, int offset, int limit) {
        List<String> history = commands.lrange(getKey(aggregation, streamId), offset, limit);
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

    private String getKey(String aggregation, String streamId) {
        return aggregation + ":" + streamId;
    }

}