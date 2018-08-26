package br.net.eventstore.publisher;

import br.net.eventstore.model.Message;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQPublisher implements Publisher, HasSubscribers {

    private ConnectionFactory factory = new ConnectionFactory();

    public RabbitMQPublisher(String rabbitURL){
        this.factory.setHost(rabbitURL);
    }

    @Override
    public Subscription subscribe(String aggregation, Subscriber subscriber) {
        return null;
    }

    @Override
    public void publish(Message message) {
        Connection connection = null;
        Channel channel = null;

        try{
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(message.getStreamId(), "fanout");

            channel.basicPublish(message.getStreamId(), message.getAggregation(), null, message.getEvent().getPayload().getBytes());
            connection.close();
        }catch (IOException | TimeoutException e){
            e.printStackTrace();
        }

    }
}
