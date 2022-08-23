package chapter07Exchanges.direct;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class DirectConsumer02 {
    private static final String EXCHANGE_NAME = "myDirect";
    private static final String QUEUE_NAME = "DirectConsumer02";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "key2");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("消息绑定的所在的队列和交换机之间的routingKey:" + message.getEnvelope().getRoutingKey());
            System.out.println("接收到信息：" + new String(message.getBody(), "UTF-8"));
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("未接收到信息");
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);

    }
}
