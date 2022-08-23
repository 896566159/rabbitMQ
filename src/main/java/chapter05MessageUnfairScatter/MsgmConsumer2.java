package chapter05MessageUnfairScatter;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MsgmConsumer2 {

    private static final String QUEUE_NAME = "scatter";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        int prefetch = 1;
        channel.basicQos(prefetch);//不公平分发,设置参数 channel.basicQos(1)，让消费者的接受消息的缓存去大小为1

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("收到消息:" + new String(message.getBody()));
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("未收到消息");
        };
        System.out.println("MsgmConsumer2已经准备好");
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);
    }

}
