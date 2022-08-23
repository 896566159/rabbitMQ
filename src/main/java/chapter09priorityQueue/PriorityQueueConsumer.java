package chapter09priorityQueue;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

public class PriorityQueueConsumer {

    private static final String QUEUE_NAME = "priorityQueue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQUtils.getChannel();

        //设置队列的最大优先级 最大可以设置到 255 官网推荐 1-10 如果设置太高比较吃内存和 CPU
        Map<String, Object> params = new HashMap();
        params.put("x-max-priority", 10);
        channel.queueDeclare(QUEUE_NAME, true, false, false, params);

        System.out.println("消费者启动等待消费..............");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String receivedMessage = new String(delivery.getBody());
            System.out.println("接收到消息:" + receivedMessage);
        };


        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消费者无法消费消息时调用，如队列被删除");
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
}

}
