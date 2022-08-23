package chapter08DeadQueue.queueFull;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

public class QueueFullConsumer {

    private static final String EXCHANGE_NAME = "QueueFull";
    private static final String QUEUE_NAME = "QueueFull";
    private static final String DEAD_EXCHANGE = "QueueFullDeadExchange";
    private static final String DEAD_QUEUE = "QueueFullDeadQueue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT, true);

        //死信交换机和死信队列绑定
        channel.queueDeclare(DEAD_QUEUE, true, false, false, null);
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "key2");

        Map<String, Object> properties = new HashMap<>();
        properties.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //正常队列设置死信 routing-key 参数 key 是固定值
        properties.put("x-dead-letter-routing-key", "key2");
        properties.put("x-max-length", 5);

        //正常的交换机和队列绑定
        channel.queueDeclare(QUEUE_NAME, true, false, false, properties);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "key1");

        System.out.println("等待接收消息........... ");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("Consumer01 接收到消息" + message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("未收到消息");
        };

        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);

    }

}
