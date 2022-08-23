package chapter02workQueues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQUtils {

    public static Channel getChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.73.128");
        factory.setUsername("admin");
        factory.setPassword("123");

        return factory.newConnection().createChannel();
    }

}
