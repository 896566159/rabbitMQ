package chapter02workQueues;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Work01 {

    private final static String QUEUE_NAME = "work01";

    public static void main(String[] args) {
        try {
            Channel channel = RabbitMQUtils.getChannel();

            DeliverCallback deliverCallback = ((consumerTag, delivery)->{
                byte[] body = delivery.getBody();
                System.out.println("work01 get the message:" + new String(body));
            });
            CancelCallback callback = (consumertag -> {
                System.out.println(consumertag + "消费者取消消费接口逻辑");
            });
            System.out.println("work02 消费者启动等待消费.................. ");
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, callback);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
