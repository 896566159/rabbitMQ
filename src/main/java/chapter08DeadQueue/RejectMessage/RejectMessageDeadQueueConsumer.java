package chapter08DeadQueue.RejectMessage;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class RejectMessageDeadQueueConsumer {

    private static final String DEAD_EXCHANGE = "rejectDeadDExchange";
    private static final String DEAD_QUEUE = "rejectDeadQueue";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();
        channel.queueDeclare(DEAD_QUEUE, true, false, false, null);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT, true);
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "key2");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("收到消息" + new String(message.getBody(), "UTF-8"));
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("未收到消息" + consumerTag);
        };

        channel.basicConsume(DEAD_QUEUE, true, deliverCallback, cancelCallback);
    }

}
