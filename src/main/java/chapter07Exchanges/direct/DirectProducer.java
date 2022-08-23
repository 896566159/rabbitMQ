package chapter07Exchanges.direct;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class DirectProducer {

    private static final String EXCHANGE_NAME = "myDirect";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME, "key1", false, null, message.getBytes("UTF-8"));
            channel.basicPublish(EXCHANGE_NAME, "key2", false, null, message.getBytes("UTF-8"));
        }
    }

}
