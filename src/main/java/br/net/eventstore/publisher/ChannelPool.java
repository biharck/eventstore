package br.net.eventstore.publisher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class ChannelPool extends GenericObjectPool<Channel> {

    public ChannelPool(String uri) {
        super(new ChannelFactory(uri));
    }


    private static class ChannelFactory extends BasePooledObjectFactory<Channel> {
        private String uri;
        private Connection conn;


        ChannelFactory(String uri) {
            this.uri = uri;
        }

        @Override
        public Channel create() throws Exception {
            Connection conn = getConnection();
            return conn.createChannel();
        }

        @Override
        public PooledObject<Channel> wrap(Channel obj) {
            return new DefaultPooledObject<>(obj);
        }

        private Connection getConnection() throws Exception{
            if (conn == null || !conn.isOpen()) {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setUri(uri);
                conn = factory.newConnection();
            }
            return conn;
        }
    }
}
