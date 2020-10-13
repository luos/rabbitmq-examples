package com.esl.uk;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQBasicProperties;
import org.slf4j.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Exercise 1 : RabbitMQ Confirm Exercise
 */
public class App {
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


            factory.getClientProperties().put("connection_name", NAME);
            factory.setPort(5672);


            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.confirmSelect();
            channel.addConfirmListener((ackTag, multiple) -> {
                // ok we dont care about confirms now
            }, (reject, tag) -> {
                // message was rejected
            });

            channel.addReturnListener((returned) -> {
                System.out.println("This message could not be routed, user is not online: " + returned.getRoutingKey() + " - " + new String(returned.getBody()));
            });

            String commonQueueName = NAME + "-common";

            // 1. Declare the common-room exchange, it should be durable, fanout

            // 1. declare an auto delete queue for receiving messages
            // 2. bind the queue to the common-room exchange


            // 1. bind the queue to the common-room exchange, routing key can be anything

            // process incoming common-room messages
            channel.basicConsume(commonQueueName, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body)
                        throws IOException {

                    String username;
                    if (properties.getHeaders() != null) {
                        username = properties.getHeaders().getOrDefault("chat-username", "Unknown user").toString();
                    } else {
                        username = "Unkown user";
                    }

                    System.out.println(username + ": " + new String(body));
                    this.getChannel().basicAck(envelope.getDeliveryTag(), false);
                }
            });

            // 1. declare an exchange for private-messages, this should be a direct exchange

            // 1. declare an exclusive queue for private-messages
            // 2. bind it to the private-messages exchange, routing key should be your name

            String privateMessagesQueueName = NAME + "-privates";

            // uncomment the following lines after the private queuei s declared

/*
            channel.basicConsume(privateMessagesQueueName, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body)
                        throws IOException {

                    String username;
                    if (properties.getHeaders() != null) {
                        username = properties.getHeaders().getOrDefault("chat-username", "Unknown user").toString();
                    } else {
                        username = "Unkown user";
                    }

                    System.out.println("[PRIVATE]" + username + ": " + new String(body));
                    this.getChannel().basicAck(envelope.getDeliveryTag(), false);
                }
            });*/


            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            boolean exit = false;
            System.out.println("Waiting for a command. Possible commands, start the line with 'send', 'exit', 'private' ");
            System.out.println("Send to common: send hello world");
            System.out.println("Send private: private Username hello world");
            while (!exit) {
                String command = reader.readLine();
                if (command.startsWith("exit")) {
                    exit = true;
                } else if (command.toLowerCase().startsWith("send")) {
                    String message = command.substring(5);

                    // 1. build a message with a header "chat-username"
                    // 2. publish it to the common-room exchange
                    Map<String, Object> headers = new HashMap<>();
                    headers.put("chat-username", NAME);
                    AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().headers(headers).build();
                    // add the publish call
                } else if (command.toLowerCase().startsWith("private")) {
                    String rest = command.substring(8);
                    Integer nextSpace = rest.indexOf(" ");
                    String targetUser = rest.substring(0, nextSpace).trim();
                    String message = rest.substring(nextSpace).trim();
                    Map<String, Object> headers = new HashMap<>();
                    headers.put("chat-username", NAME);
                    System.out.println("Sending to '" + targetUser + "'");
                    AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().headers(headers).build();
                    channel.basicPublish("private-messages", targetUser, true, props, message.getBytes());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
