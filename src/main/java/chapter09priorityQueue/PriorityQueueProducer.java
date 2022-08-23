package chapter09priorityQueue;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

public class PriorityQueueProducer {

    private static final String QUEUE_NAME="priorityQueue";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();

        //给消息赋予一个 priority 属性
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(5).build();

        for (int i = 0; i < 100; i++) {
            String msg = "info" + i;

            if (i % 5 == 0) {
                channel.basicPublish("", QUEUE_NAME, properties, msg.getBytes());
            } else {
                channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            }

            System.out.println("发送消息完成:" + msg);
        }
    }
}
