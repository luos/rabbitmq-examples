package com.esl.uk;

import com.rabbitmq.client.*;
import org.slf4j.*;

import java.io.IOException;

/**
 * Exercise 1 : RabbitMQ Confirm Exercise
 */
public class App {
    final private static String QUEUE = "test-queue";
    final private static String NAME = "PUT_YOUR_NAME_HERE";

    public static void main(String[] args) {
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            Logger logger = LoggerFactory.getLogger(App.class);

            factory.setAutomaticRecoveryEnabled(true);

            // Set 'Connection' Credentials
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setHost("localhost");

            factory.setPort(5672);

            factory.getClientProperties().put("connection_name", NAME);

            logger.info("Setting up consumer on " + factory.getHost() + " from " + QUEUE);

            Connection consumerConnection = factory.newConnection();
            Channel consumerChannel = consumerConnection.createChannel();

            consumerChannel.basicQos(5, true);

            consumerChannel.basicConsume(QUEUE, true, new DefaultConsumer(consumerChannel) {
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
                            Thread.sleep((int) (Math.random() * 2500));
                            // this.getChannel().basicAck(envelope.getDeliveryTag(), false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            });

            System.out.println("Waiting...");
            Thread.sleep(100000);


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
