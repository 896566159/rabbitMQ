package chapter08DeadQueue.queueFull;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class QueueFullDeadConsumer {

    private static final String DEAD_EXCHANGE = "QueueFullDeadExchange";
    private static final String DEAD_QUEUE = "QueueFullDeadQueue";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();
        channel.queueDeclare(DEAD_QUEUE, true, false, false, null);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT, true);

        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "key2");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("收到消息：" + new String(message.getBody(), "UTF-8"));
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("消息未接受到");
        };

        channel.basicConsume(DEAD_QUEUE, true, deliverCallback, cancelCallback);
    }

}
