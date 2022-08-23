package chapter03MessageResponse;

import com.rabbitmq.client.Channel;
import chapter02workQueues.RabbitMQUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class MessageProducer {

    private static final String QUEUEU_NAME = "basicACK_Nack_Reject";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        channel.queueDeclare(QUEUEU_NAME, false, false, false, null);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish("", QUEUEU_NAME, null, message.getBytes("UTF-8"));
            System.out.println("消息：" + message + "已发送");
        }
    }
}
