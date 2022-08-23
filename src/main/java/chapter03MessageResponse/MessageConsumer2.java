package chapter03MessageResponse;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import chapter02workQueues.RabbitMQUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MessageConsumer2 {

    private static final String QUEUE_NAME = "basicACK_Nack_Reject";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        DeliverCallback deliverCallback = (((consumerTag, message) -> {
            System.out.println("消息接收回调函数（改接口方法中可接受到消息，消息处理业务可写在此方法中");
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("此次受到的消息：" + new String(message.getBody()));
            System.out.println("此次受到的消息封皮Envelope：" + message.getEnvelope());
            //默认消息采用的是自动应答，所以我们要想实现消息消费过程中不丢失，需要把自动应答改为手动应答
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);//设置为手动应答自动应答
            System.out.println("此次受到的消息参数Properties：" + message.getProperties());
        }));
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("消息未被接收到（该接口方法中可未接受到消息，未接受到消息，需要的处理业务可写在此方法中");
        };

        System.out.println("MessageConsumer2已经准备好接受消息");
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }

}
