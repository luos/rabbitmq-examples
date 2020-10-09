package com.esl.uk;

import com.rabbitmq.client.*;
import org.slf4j.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Exercise 1 : RabbitMQ Confirm Exercise
 */
public class App {
    final private static String QUEUE = "EXERCISE_1_TEST_QUEUE";
    final private static String EXCHANGE = "EXERCISE_1_DIRECT_EXCHANGE";
    final private static String RK = "EXERCISE_1_ROUTING_KEY";
    final private static String NAME = "PUT_YOUR_NAME_HERE";

    public static void main(String[] args) {
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            Logger logger = LoggerFactory.getLogger(App.class);

            factory.setAutomaticRecoveryEnabled(true);

            // Set 'Connection' Credentials
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.getClientProperties().put("connection_name", NAME);

            factory.setHost("localhost");
            factory.setPort(5672);

            logger.info("Setting up producer ...");

            // Create Connection and
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            ConcurrentHashMap<Long, String> outstandingMessages = new ConcurrentHashMap<>();


            channel.addConfirmListener((tag, multiple) -> {
                // handle acknowledgments
                outstandingMessages.remove(tag);
                List<Long> allKeys = Collections.list(outstandingMessages.keys());

                if (multiple) {
                    for (Long t : allKeys) {
                        if (t < tag) {
                            outstandingMessages.remove(t);
                        }
                    }
                }

                System.out.println("Received confirm for tag " + tag + " multiple: " + multiple);
            }, (tag, multiple) -> {
                // handle rejections, we will not do that now
                System.out.println("Received reject for tag " + tag + " multiple: " + multiple);
            });


            // Create Queue
            channel.queueDeclare(QUEUE, true, false, false, null);

            // Create Exchange
            channel.exchangeDeclare(EXCHANGE, "direct");

            // Bind Queue to Exchange
            channel.queueBind(QUEUE, EXCHANGE, RK);

            logger.info("Start publishing messages...");

            // Publish 100 messages
            int n = 0;

            // enable confirms
            channel.confirmSelect();

            for (n = 0; n < 100; n++) {
                String message = "Published message: " + n;
                outstandingMessages.put(channel.getNextPublishSeqNo(), message);
                channel.basicPublish(EXCHANGE, RK, null, message.getBytes());
            }

            // add waiting for confirms

            while (!outstandingMessages.isEmpty()) {
                System.out.println("We are waiting for confirms for messages: " + outstandingMessages.size());
                try {
                    channel.waitForConfirms(1);
                } catch (Exception e) {
                    System.out.println("Timeout Exception");
                }
            }

            logger.info("Completed publishing {} messages ...", n);
            Thread.sleep(5000);
            // conn create
            // channel create
            // consume
            Connection consumerConnection = factory.newConnection();
            Channel consumerChannel = consumerConnection.createChannel();

            consumerChannel.basicConsume(QUEUE, new DefaultConsumer(consumerChannel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body)
                        throws IOException {
                    new Thread(() -> {
                        try {
                            // Simulate Thread scheduling randomness
                            Thread.sleep((int) (Math.random() * 5));
                            System.out.println("Received message: " + new String(body) + " Delivery tag: " + envelope.getDeliveryTag());
                            // Simulate hard work
                            Thread.sleep(2500);
                            this.getChannel().basicAck(envelope.getDeliveryTag(), false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            });

            System.out.println("Waiting...");
            Thread.sleep(100000);
            connection.close(5000);


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
