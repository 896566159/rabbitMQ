package chapter07Exchanges.fanout;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReciveLog01 {

    private static final String EXCCHANGE_NAME = "fanout";
    private static final String QUEUE_NAME = "fanout01";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        channel.exchangeDeclare("fanout", BuiltinExchangeType.FANOUT, true, false, false, null);
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.exchangeBind(EXCCHANGE_NAME, QUEUE_NAME,  "");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("ReciveLog01收到信息：" + new String(message.getBody(), "utf-8"));
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("ReciveLog01未收到消息回调函数");
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }

}
