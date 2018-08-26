package br.net.eventstore.publisher;

import br.net.eventstore.model.Message;
import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQPublisher implements Publisher, HasSubscribers {

    private ConnectionFactory factory = new ConnectionFactory();
    private final String EXCHANGE_NAME = "EVENT_STORE";
    private Gson serializer;

    public RabbitMQPublisher(String rabbitURL){
        factory.setHost(rabbitURL);
        serializer = new Gson();
    }

    @Override
    public Subscription subscribe(String aggregation, Subscriber subscriber) {
        Connection connection = null;
        Channel channel = null;

        try{
            connection = factory.newConnection();
            channel = connection.createChannel();

            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, aggregation);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                }
            };
            channel.basicConsume(queueName, true, consumer);

            connection.close();
        }catch (IOException | TimeoutException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void publish(Message message) {
        Connection connection = null;
        Channel channel = null;

        try{
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            channel.basicPublish(EXCHANGE_NAME, message.getAggregation(), null, serializer.toJson(message.getEvent().getPayload()).getBytes());
            channel.close();
            connection.close();
        }catch (IOException | TimeoutException e){
            e.printStackTrace();
        }

    }

}