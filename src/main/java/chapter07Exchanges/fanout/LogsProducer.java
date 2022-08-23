package chapter07Exchanges.fanout;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;


public class LogsProducer {

    private static final String EXCCHANGE_NAME = "fanout";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQUtils.getChannel();
        channel.exchangeDeclare(EXCCHANGE_NAME, BuiltinExchangeType.FANOUT, true, false, false, null);
        channel.confirmSelect();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish(EXCCHANGE_NAME, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("utf-8"));
            boolean b = channel.waitForConfirms();

            if (b) {
                System.out.println("消息" + message + "发送成功");
            }
        }
    }

}
