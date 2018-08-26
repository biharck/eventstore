package br.net.eventstore.publisher;

import com.google.gson.Gson;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import br.net.eventstore.model.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A Publisher that use Redis pub / sub feature to message communications.
 */
public class RedisPublisher implements Publisher, HasSubscribers {

    private static Lock writeLock = new ReentrantLock();

    private ConcurrentHashMap<String, List<Subscriber>> listeners = new ConcurrentHashMap<>();
    private final RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, String> connection;
    private RedisPubSubAsyncCommands<String, String> commands;
    private Gson serializer;
    private RedisPubSubListener<String, String> listener;

    public RedisPublisher(RedisClient redisClient) {
        this.redisClient = redisClient;
        connection = redisClient.connectPubSub();
        commands = connection.async();
        serializer = new Gson();
    }

    public RedisPublisher(String redisURL) {
        this(RedisClient.create(redisURL));
    }

    @Override
    public void publish(Message message) {
        commands.publish(message.getAggregation(), serializer.toJson(message));
    }

    @Override
    public Subscription subscribe(String aggregation, Subscriber subscriber) {
        ensureRedisListener();
        List<Subscriber> aggregateListeners = listeners.computeIfAbsent(aggregation,
                key -> {
                    commands.subscribe(aggregation);
                    return Collections.synchronizedList(new ArrayList<>());
                });
        aggregateListeners.add(subscriber);


        return () -> {
            commands.unsubscribe(aggregation);
            aggregateListeners.remove(subscriber);
        };
    }

    private void ensureRedisListener() {
        if (listener == null) {
            writeLock.lock();
            if (listener == null) {
                try {
                    listener = new RedisPubSubListener<String, String>() {
                        @Override
                        public void message(String aggregation, String received) {
                            Message message = serializer.fromJson(received, Message.class);
                            List<Subscriber> aggregateListeners = listeners.get(message.getAggregation());
                            if (aggregateListeners != null) {
                                aggregateListeners.forEach(subscriber -> subscriber.on(message));
                            }
                        }

                        @Override
                        public void message(String pattern, String channel, String message) { }

                        @Override
                        public void subscribed(String channel, long count) { }

                        @Override
                        public void psubscribed(String pattern, long count) { }

                        @Override
                        public void unsubscribed(String channel, long count) { }

                        @Override
                        public void punsubscribed(String pattern, long count) { }
                    };

                    connection.addListener(listener);
                } finally {
                    writeLock.unlock();
                }
            }
        }
    }
}
