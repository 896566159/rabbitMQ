package chapter06Confirm;

import chapter02workQueues.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

public class ConfirmProducer {

    private static final String QUEUE_NAME1 = "individualConfirm";
    private static final String QUEUE_NAME2 = "batchConfirm";
    private static final String QUEUE_NAME3 = "AsynchronizedConfirm";
    private static final int messageCount = 10000;

    public static void main(String[] args) throws Exception {
//        long start = System.currentTimeMillis();
//        individualConfirm();
//        long end = System.currentTimeMillis();
//        System.out.println(end - start);
//        long start = System.currentTimeMillis();
//        batchConfirm();
//        long end = System.currentTimeMillis();
//        System.out.println(end - start);
        long start = System.currentTimeMillis();
        asynchronizedConfirm();
        long end = System.currentTimeMillis();
        System.out.println(end - start);

    }

    /**
     * 单个确认发布
     *  这是一种简单的确认方式，它是一种同步确认发布的方式，也就是发布一个消息之后只有它
     *  被确认发布，后续的消息才能继续发布,waitForConfirmsOrDie(long)这个方法只有在消息被确认
     *  的时候才返回，如果在指定时间范围内这个消息没有被确认那么它将抛出异常。
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    public static void individualConfirm() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMQUtils.getChannel();
        //1.确认发布模式先要保证信道是持久化的
        channel.queueDeclare(QUEUE_NAME1, true, false, false, null);
        //3.设置信道是信息发布确认模式
        channel.confirmSelect();

        for (int i = 0; i < messageCount; i++) {
            String message = String.valueOf(i);
            //2.设置发信息时，信息是持久化的
            channel.basicPublish("", QUEUE_NAME1, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));

            //单次确认消息
            boolean waitForConfirms = channel.waitForConfirms();
            if (!waitForConfirms) {
                System.out.println("信息未发送成功，重新发送");
            }
        }

    }

    /**
     * 批量确认发布
     *  上面那种方式非常慢，与单个等待确认消息相比，先发布一批消息然后一起确认可以极大地
     *  提高吞吐量，当然这种方式的缺点就是:当发生故障导致发布出现问题时，不知道是哪个消息出现
     *  问题了，我们必须将整个批处理保存在内存中，以记录重要的信息而后重新发布消息。当然这种
     *  方案仍然是同步的，也一样阻塞消息的发布
     * @throws Exception
     */
    public static void batchConfirm() throws Exception {
        Channel channel = RabbitMQUtils.getChannel();
        //1.确认发布模式先要保证信道是持久化的
        channel.queueDeclare(QUEUE_NAME2, true, false, false, null);
        //3.设置信道是信息发布确认模式
        channel.confirmSelect();
        int unconfirmCOunt = 0;//未确认信息数量

        for (int i = 0; i < messageCount; i++) {
            String message = String.valueOf(i);
            //2.设置发信息时，信息是持久化的
            channel.basicPublish("", QUEUE_NAME2, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));

            //每隔一百条信息批量确认一次
            if (i % 99 == 0) {
                boolean waitForConfirms = channel.waitForConfirms();
                unconfirmCOunt++;
                if (!waitForConfirms) {
                    System.out.println("信息未发送成功，重新发送");
                    unconfirmCOunt = 0;
                }
            }
        }

        //如果未确认的信息还有，再次发送
        if (unconfirmCOunt > 0) {
            channel.waitForConfirms();
        }

    }

    public static void asynchronizedConfirm() throws Exception {
        Channel channel = RabbitMQUtils.getChannel();
        //1.确认发布模式先要保证信道是持久化的
        channel.queueDeclare(QUEUE_NAME3, true, false, false, null);
        //3.设置信道是信息发布确认模式
        channel.confirmSelect();
        /**
         * 线程安全有序的一个哈希表，适用于高并发的情况
         * 1.轻松的将序号与消息进行关联
         * 2.轻松批量删除条目 只要给到序列号
         * 3.支持并发访问
         */
        //key是发送信息时，信道的通信的数量标记号channel.getNextPublishSeqNo()， value是消息
        ConcurrentSkipListMap<Long, String> messageMap = new ConcurrentSkipListMap<>();

        /**
         * 信息去人成功的回调函数，该方法被调用证明消息已经被确认
         * 已经被确认的消息可以从map中删除
         *  * 1.消息序列号
         *  * 2.true 可以确认小于等于当前序列号的消息
         *      false 确认当前序列号消息
         */
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            if (multiple) {
                //返回的是小于等于当前序列号的未确认消息 是一个 map
                ConcurrentNavigableMap<Long, String> confirmed = messageMap.headMap(deliveryTag, true);
                //清除该部分未确认消息
                confirmed.clear();
            }else{
                //只清除当前序列号的消息
                messageMap.remove(deliveryTag);
            }
        };

        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            System.out.println("序列号为" + deliveryTag + "的信息未发送成功");
        };

        /**
         * 添加一个异步确认的监听器
         * 1.确认收到消息的回调
         * 2.未收到消息的回调
         */
        channel.addConfirmListener(ackCallback, nackCallback);

        for (int i = 0; i < messageCount; i++) {
            String message = String.valueOf(i);
            messageMap.put(channel.getNextPublishSeqNo(), message);//将发送的信息都保存在map中
            //2.设置发信息时，信息是持久化的
            channel.basicPublish("", QUEUE_NAME3, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
        }

    }

}
