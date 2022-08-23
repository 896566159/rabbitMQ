package chapter07Exchanges.topic;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

public class TopicProducer {

    private static final String EXCHAANGE_NAME = "myTopics";

    /**
     * 该生产者的连接 myTopics 交换机，消息发送到三个 routingKey 匹配的队列
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();
        channel.exchangeDeclare(EXCHAANGE_NAME, BuiltinExchangeType.TOPIC, true, false, false, null);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();

            channel.basicPublish(EXCHAANGE_NAME, "rabbitmq.topic.key1", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("utf-8"));
            channel.basicPublish(EXCHAANGE_NAME, "mq.topic.key2", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("utf-8"));
            channel.basicPublish(EXCHAANGE_NAME, "topic.key.nothing", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("utf-8"));
        }
    }

}
