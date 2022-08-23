package chapter1HelloWord;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.73.128");
        factory.setUsername("admin");
        factory.setPassword("123");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            System.out.println("Consumer wait message");
            //推送的消息如何进行消费的接口回调
            DeliverCallback deliverCallback = ((consumerTag, delivery)->{
                String message = new String(delivery.getBody());
                System.out.println("Consumer get the message:" + message);
            });

            //取消消费的一个回调接口 如在消费的时候队列被删除掉了
            CancelCallback cancelCallback = ((consumer)->{
                System.out.println("message interrupt");
            });

            /**
             * 消费者消费消息basicConsume()
             * 1.消费那个队列
             * 2.消费成功之后是否自动应答，true代表自动应答，false表示手动应答
             * 3。消费者未成功消费的回调
             */
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
