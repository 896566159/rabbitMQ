package chapter08DeadQueue.TTL;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 正常的，按道理必须消费来自TTLProducer的消息
 */
public class TTLNormalConsumer {

    private static final String EXHANGE_NAME = "TTLProducer";
    //死信交换机名称
    private static final String DEAD_EXCHANGE = "dead_exchange";
    private static final String DEAD_NAME = "dead_queue";
    private static final String QUEUE_NAME = "TTLConsumer";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQUtils.getChannel();

        //声明死信和普通交换机 类型为 direct
        channel.exchangeDeclare(EXHANGE_NAME, BuiltinExchangeType.DIRECT, true);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT, true);

        //声明死信队列
        channel.queueDeclare(DEAD_NAME, true, false, false, null);
        //死信队列绑定死信交换机与 routingkey
        channel.queueBind(DEAD_NAME, DEAD_EXCHANGE, "key2");

        //正常队列绑定死信队列信息
        Map<String, Object> params = new HashMap<>();
        //正常队列设置死信交换机 参数 key 是固定值
        params.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //正常队列设置死信 routing-key 参数 key 是固定值
        params.put("x-dead-letter-routing-key", "key2");

        //正常队列绑定与正常交换机绑定
        channel.queueDeclare(QUEUE_NAME, true, false, false, params);
        channel.queueBind(QUEUE_NAME, EXHANGE_NAME, "key1");

        System.out.println("等待接收消息........... ");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("Consumer01 接收到消息" + message);
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("Consumer01 未收到消息息");
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);

    }

}
