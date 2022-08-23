package chapter07Exchanges.topic;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class TopicConsumer03 {

    private static final String EXCHAANGE_NAME = "myTopics";
    private static final String QUEUE_NAME = "TopicConsumer03";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.exchangeDeclare(EXCHAANGE_NAME, BuiltinExchangeType.TOPIC, true);
        channel.queueBind(QUEUE_NAME, EXCHAANGE_NAME, "*.key.*");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("TopicConsumer03接收到的信息匹配key是：" + message.getEnvelope().getRoutingKey() + "， 信息内容是：" + new String(message.getBody(), "UTF-8"));
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("TopicConsumer03未收到序列号" + consumerTag);
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }

}
