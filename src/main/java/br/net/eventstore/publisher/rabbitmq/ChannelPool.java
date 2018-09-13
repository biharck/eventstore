package br.net.eventstore.publisher.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class ChannelPool extends GenericObjectPool<Channel> {

    public ChannelPool(String uri) {
        super(new ChannelFactory(uri));
    }

    public ChannelPool(String uri, GenericObjectPoolConfig config) {
        super(new ChannelFactory(uri), config);
    }


    private static class ChannelFactory extends BasePooledObjectFactory<Channel> {
        private String uri;
        private Connection conn;


        ChannelFactory(String uri) {
            this.uri = uri;
        }

        @Override
        public Channel create() {
            Connection connection = getConnection();
            try {
                return connection.createChannel();
            } catch (Exception e) {
                throw new ChannelException("Can not open a channel with rabbitmq", e);
            }
        }

        @Override
        public PooledObject<Channel> wrap(Channel obj) {
            return new DefaultPooledObject<>(obj);
        }

        private Connection getConnection() {
            try {
                if (conn == null || !conn.isOpen()) {
                    ConnectionFactory factory = new ConnectionFactory();
                    factory.setUri(uri);
                    conn = factory.newConnection();
                }
                return conn;
            } catch (Exception e) {
                throw new ConnectionException("Can not create connection with rabbitmq", e);
            }
        }

        @Override
        public boolean validateObject(PooledObject<Channel> channel) {
            Channel ch = channel.getObject();
            return ch != null && ch.isOpen();
        }
    }
}
