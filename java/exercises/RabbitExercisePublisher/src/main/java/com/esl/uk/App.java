package com.esl.uk;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQBasicProperties;
import org.slf4j.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This application sends `numberOfMessages` messages and waits sometimes between the messages.
 */
public class App {
    final private static String NAME = "PUT_YOUR_NAME_HERE";
    final private static String QUEUE = NAME + "-queue";

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

            logger.info("Setting up publisher on " + factory.getHost() + " from " + QUEUE);

            Integer numberOfMessages = 200;
            Integer waitBetweenMessagesMs = 1000;
            Integer messageSizeBytes = 1024 * 100;
            byte[] messageBody = new byte[messageSizeBytes];
            new Random().nextBytes(messageBody);

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.confirmSelect();
            connection.addBlockedListener((String reason) -> {
                System.out.println("\nConnection is blocking because: " + reason);
            }, () -> {
                System.out.println("\nConnection unblocked.");
            });
            channel.queueDeclare(QUEUE, true, false, false, null);

            // persistent messages
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().deliveryMode(2).build();

            for (int n = 0; n < numberOfMessages; n++) {
                System.out.print(".");
                channel.basicPublish("", QUEUE, false, props, messageBody);
                Thread.sleep(waitBetweenMessagesMs);
            }

            System.out.println("\nWaiting for confirms");

            channel.waitForConfirms();

            System.out.println("\nFinished publishing");

            connection.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
