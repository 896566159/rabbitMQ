package chapter08DeadQueue.RejectMessage;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 在这个类中，拒收某一些消息，导致这些消息到死信队列中
 */
public class RejectMessageConsumer01 {

    private static final String EXCHANGE_NAME = "rejectExchange";
    private static final String QUEUE_NAME = "rejectQueue";
    private static final String DEAD_EXCHANGE = "rejectDeadDExchange";
    private static final String DEAD_QUEUE = "rejectDeadQueue";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();
        //声明两个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT, true);

        //死信队列和死信交换机绑定
        channel.queueDeclare(DEAD_QUEUE, true, false, false, null);
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "key2");

        //设置正常队列的一下参数
        Map<String, Object> properties = new HashMap<>();
        properties.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //正常队列设置死信 routing-key 参数 key 是固定值
        properties.put("x-dead-letter-routing-key", "key2");

        //正常队列和正常交换机绑定
        channel.queueDeclare(QUEUE_NAME, true, false, false, properties);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "key1");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String info = new String(message.getBody());
            if (info.equals("key2")) {
                System.out.println("拒收该消息" + info);
                //requeue 设置为 false 代表拒绝重新入队 该队列如果配置了死信交换机将发送到死信队列中
                channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
            } else {
                System.out.println("接受该消息" + info);
                //requeue 设置为 false 代表拒绝重新入队 该队列如果配置了死信交换机将发送到死信队列中
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }

        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("未收到消息");
        };

        channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);

    }

}
