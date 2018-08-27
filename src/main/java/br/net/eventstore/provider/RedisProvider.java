package br.net.eventstore.provider;

import br.net.eventstore.model.Event;
import com.google.gson.Gson;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A Persistence Provider that handle all the data in redis.
 */
public class RedisProvider implements Provider{

    private final RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, String> connection;
    private RedisCommands<String, String> commands;
    private Gson serializer;

    public RedisProvider(RedisClient redisClient) {
        this.redisClient = redisClient;
        connection = redisClient.connectPubSub();
        commands = connection.sync();
        serializer = new Gson();
    }

    public RedisProvider(String redisURL) {
        this(RedisClient.create(redisURL));
    }

    @Override
    public Event addEvent(String aggregation, String streamId, Event event){

        event.setSequence(commands.incr("sequences:{" + getKey(aggregation, streamId) + "}")-1);
        event.setCommitTimestamp(Long.parseLong(commands.time().get(0)));

        commands.rpush(getKey(aggregation, streamId), serializer.toJson(event));
        return event;
    }

    @Override
    public List<Event> getEvents(String aggregation, String streamId){
        List<String> history = commands.lrange(getKey(aggregation, streamId), 0, -1);
        return history.stream().map(data -> serializer.fromJson(data, Event.class)).collect(Collectors.toList());
    }

    private String getKey(String aggregation, String streamId) {
        return aggregation + ":" + streamId;
    }

}