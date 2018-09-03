package br.net.eventstore.publisher.rabbitmq;

import com.rabbitmq.client.Channel;
import io.lettuce.core.RedisClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ChannelPoolTest {

    private ChannelPool pool;
    private AtomicInteger count = new AtomicInteger(0);

    @Before
    public void setUp() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(1);
        config.setMaxTotal(1);
        /*---------------------------------------------------------------------+
        |TestOnBorrow=true --> To ensure that we get a valid object from pool  |
        |TestOnReturn=true --> To ensure that valid object is returned to pool |
        +---------------------------------------------------------------------*/
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        pool = new ChannelPool("amqp://localhost", config);
    }

    @Test
    public void test() {
        try {
            int limit = 10;
            ExecutorService es = new ThreadPoolExecutor(10,
                                                    10,
                                                        0L,
                                                        TimeUnit.MILLISECONDS,
                                                        new ArrayBlockingQueue<>(limit));
            for (int i=0; i<limit; i++) {
                Runnable r = () -> {
                    Channel channel = null;
                    try {
                        channel = pool.borrowObject();
                        count.getAndIncrement();
                        assertThat(channel.isOpen(), is(true));
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    } finally {
                        if (channel != null) {
                            pool.returnObject(channel);
                        }
                    }
                };
                es.submit(r);
            }
            es.shutdown();
            try {
                es.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException ignored) {}
            System.out.println("Pool Stats:\n Created:[" + pool.getCreatedCount() + "], Borrowed:[" + pool.getBorrowedCount() + "]");
            assertThat(limit, is(count.get()));
            assertThat(Long.valueOf(count.get()), is(pool.getBorrowedCount()));
            assertThat(1l, is(pool.getCreatedCount()));
        } catch (Exception ex) {
            fail("Exception:" + ex);
        }
    }
}
