package chapter04MessageDurable;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class MsgProducer {

    private static final String QUEUE_NAME = "durable_persistent";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        boolean durable = true;//在声明队列的时候把 durable 参数设置为持久化
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        Scanner scanner = new Scanner(System.in);
        System.out.println("channel已经准备好，可以发送消息");
        while (scanner.hasNext()) {
            String message = scanner.next();
            System.out.println("消息" + message + " 已经发送");
//            要想让消息实现持久化需要在消息生产者修改代码，MessageProperties.PERSISTENT_TEXT_PLAIN 添 加这个属性。
            channel.basicPublish("",
                                    QUEUE_NAME,
                                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                                    message.getBytes("UTF-8"));
        }
    }
}
