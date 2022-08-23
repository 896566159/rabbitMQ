package chapter1HelloWord;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.73.128");
        factory.setUsername("admin");
        factory.setPassword("123");
        //channel实现了自动close，不需要手动的close()
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            /**
             * queueDeclare()方法：
             * 1、队列名字
             * 2、队列中的消息是否需要持久化
             * 3、该队列是否只提供一个消费者进行消费。是否进行共享，true可以多个消费者消费
             * 4、是否自动删除，最后一个消费者断开连接之后，该队列是否自动删除，true自动删除
             * 5.其他参数
             */
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "hello, this is from producer";
            System.out.println("字符串调用getBytes()方法：" + message.getBytes());

            /**
             * 发送一个消息：basicPubish():
             * 1。发送到哪个交换机
             * 2。路由中的key是哪个
             * 3、其他的参数
             * 4.发送的消息
             */
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("message over");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
