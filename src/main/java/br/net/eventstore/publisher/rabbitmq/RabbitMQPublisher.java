package br.net.eventstore.publisher.rabbitmq;

import br.net.eventstore.publisher.Publisher;
import br.net.eventstore.publisher.Subscriber;
import br.net.eventstore.publisher.Subscription;
import br.net.eventstore.publisher.HasSubscribers;
import br.net.eventstore.model.Message;
import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * A Publisher that use Redis pub / sub feature to message communications.
 */
public class RabbitMQPublisher implements Publisher, HasSubscribers {

    private ChannelPool channels;
    private Gson serializer;


    public RabbitMQPublisher(String uri) {
        channels = new ChannelPool(uri);
        serializer = new Gson();
    }

    @Override
    public void publish(Message message) {
        Channel channel = null;
        try {
            channel = channels.borrowObject();

            channel.exchangeDeclare(message.getAggregation(), "fanout");

            channel.basicPublish(message.getAggregation(), "", null, serializer.toJson(message).getBytes());
        } catch (Exception e) {

        } finally {
             if (channel != null) {
                 channels.returnObject(channel);
             }
        }
    }

    @Override
    public Subscription subscribe(String aggregation, Subscriber subscriber) {
        Channel channel = null;
        try {
            channel = channels.borrowObject();

            channel.exchangeDeclare(aggregation, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, aggregation, "");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String received = new String(body, "UTF-8");
                    Message message = serializer.fromJson(received, Message.class);
                    subscriber.on(message);
                }
            };
            channel.basicConsume(queueName, true, consumer);
            return () -> {
                Channel ch = null;
                try {
                    ch = channels.borrowObject();
                    ch.basicCancel(((DefaultConsumer) consumer).getConsumerTag());
                } catch (Exception e) {

                } finally {
                    if (ch != null){
                        channels.returnObject(ch);
                    }
                }
                return;
            };
        } catch (Exception e) {
            return null;
        } finally {
            if (channel != null) {
                channels.returnObject(channel);
            }
        }

    }
}
