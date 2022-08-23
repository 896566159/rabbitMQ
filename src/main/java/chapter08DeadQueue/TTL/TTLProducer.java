package chapter08DeadQueue.TTL;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

public class TTLProducer {

    private static final String EXHANGE_NAME = "TTLProducer";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();
        channel.exchangeDeclare(EXHANGE_NAME, BuiltinExchangeType.DIRECT, true);

        //设置消息TTL
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();

            //设置消息过期参数
            channel.basicPublish(EXHANGE_NAME, "key1", properties, message.getBytes("utf-8"));
        }
    }

}
