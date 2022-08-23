package chapter05MessageUnfairScatter;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class MsgmProducer {

    private static final String QUEUE_NAME = "scatter";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            System.out.println("消息" + message + "已经发送");

            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        }
    }

}
