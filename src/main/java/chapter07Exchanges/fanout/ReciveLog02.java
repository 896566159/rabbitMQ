package chapter07Exchanges.fanout;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ReciveLog02 {

    private static final String EXCHANE_NAME = "fanout";
    private static final String QUEUEU_NAME = "fanout02";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();
        channel.exchangeDeclare("fanout", BuiltinExchangeType.FANOUT, true, false, false, null);
        channel.queueDeclare(QUEUEU_NAME, true, false, false, null);
        channel.exchangeBind("fanout02", "fanout", "");


        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("ReciveLog02收到信息：" + new String(message.getBody(), "utf-8"));
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("ReciveLog02未收到序列" + consumerTag);
        };

        channel.basicConsume(QUEUEU_NAME, true, deliverCallback, cancelCallback);
    }
}
