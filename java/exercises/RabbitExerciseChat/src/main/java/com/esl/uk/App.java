package com.esl.uk;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQBasicProperties;
import org.slf4j.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Exercise 1 : RabbitMQ Confirm Exercise
 */
public class App {
    final private static String NAME = "Lajos";

    public static void main(String[] args) {
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            Logger logger = LoggerFactory.getLogger(App.class);

            factory.setAutomaticRecoveryEnabled(true);

            // Set 'Connection' Credentials
            factory.setUsername("user");
            factory.setPassword("password");
            factory.setHost("35.158.11.199");

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

            // 1. Declare the common room exchange, it should be durable, fanout
            channel.exchangeDeclare("common-room", "fanout", true);

            // 1. declare an auto delete queue for receiving messages
            // 2. bind the queue to the common-room exchange

            channel.queueDeclare(commonQueueName, true, false, true, null);

            // 1. bind the queue to the common-room exchange, routing key can be anything
            channel.queueBind(commonQueueName, "common-room", "");

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

                    System.out.println("[" + new Date() + "] " + username + ": " + new String(body));
                    this.getChannel().basicAck(envelope.getDeliveryTag(), false);
                }
            });

            // 1. declare an exchange for private-messages, this should be a direct exchange
            channel.exchangeDeclare("private-messages", "direct", true);

            // 1. declare an exclusive queue for private-messages
            // 2. bind it to the private-messages exchange, routing key should be your name

            String privateMessagesQueueName = NAME + "-privates";
            channel.queueDeclare(privateMessagesQueueName, true, true, true, null);
            channel.queueBind(privateMessagesQueueName, "private-messages", NAME);

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

                    System.out.println("[" + new Date() + "] " + "[PRIVATE]" + username + ": " + new String(body));
                    this.getChannel().basicAck(envelope.getDeliveryTag(), false);
                }
            });

            // **** Delayed Messages with Delayed Exchange

            // setup of delayed message sending with delayed-exchange
            // declare exchange of type x-delayed-message, for example "NAME-delayed-exchange"
            // the x-delayed-type of the exchange should be fanout
            // bind the exchange to the common-room exchange
            String delayedExchangeName = NAME + "-delayed";

            HashMap<String, Object> delayedArguments = new HashMap<>();
            delayedArguments.put("x-delayed-type", "fanout");

            // 1. declare exchange channel.exchangeDeclare(name, type, durable=true, autoDelete=false, delayedArguments)
            channel.exchangeDeclare(delayedExchangeName,"x-delayed-message", true, false, delayedArguments);

            // 2. bind exchange to common-room, channel.exchangeBind(destination, source, routingKey)
            channel.exchangeBind("common-room", delayedExchangeName, "");



            // **** Delayed Messages with Dead Lettering

            String messagesWithTtlQueue = NAME + "-expiring-messages";

            // 1. declare a queue to hold the messages which will be published with



            // **** message command handling

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            boolean exit = false;
            System.out.println("Waiting for a command. Possible commands, start the line with 'send', 'exit', 'private' ");
            System.out.println("Send to common: send hello world");
            System.out.println("Send private: private Username hello world");
            System.out.println("Send delayed through delay-exchange: delay-exchange 1000 message");
            System.out.println("Send delayed through delay-dead-letter: delay-dead-letter 1000 message");
            while (!exit) {
                String command = reader.readLine();
                if (command.startsWith("exit")) {
                    exit = true;
                } else if (command.toLowerCase().startsWith("send")) {
                    String message = command.substring(5);

                    // 1. build a message with a header "chat-username"
                    // 2. publish it to the common-room exchange

                    Map<String, Object> headers = getDefaultHeaders();
                    AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().headers(headers).build();
                    channel.basicPublish("common-room", "", props, message.getBytes());
                } else if (command.toLowerCase().startsWith("private")) {
                    String rest = command.substring(8);
                    Integer nextSpace = rest.indexOf(" ");
                    String targetUser = rest.substring(0, nextSpace).trim();
                    String message = rest.substring(nextSpace).trim();
                    Map<String, Object> headers = getDefaultHeaders();
                    System.out.println("Sending to '" + targetUser + "'");
                    AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().headers(headers).build();
                    channel.basicPublish("private-messages", targetUser, true, props, message.getBytes());
                } else if (command.toLowerCase().startsWith("delay-exchange")) {
                    Pattern pattern = Pattern.compile("delay-exchange ([0-9]+) (.*)", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(command);
                    if (matcher.matches()) {
                        int delay = Integer.parseInt(matcher.group(1));
                        String message = matcher.group(2);
                        String fullMessage = "[DELAYED] " + message + " - sent on " + new Date();
                        System.out.println("Sending message with delay: " + delay + "ms - " + message);

                        // get headers with name
                        Map<String, Object> headers = getDefaultHeaders();
                        headers.put("x-delay", delay);
                        // 1. add x-delay to the headers
                        // headers.put ... x-delay delay
                        // 2. build the amqp message properties out of the headers, see for "private-messages" publish how to do it
                        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                                .headers(headers)
                                .build();
                        // publish message
                         channel.basicPublish(delayedExchangeName,"", false, props, fullMessage.getBytes());
                    } else {
                        System.out.println("Unknown command.");
                    }
                } else if (command.toLowerCase().startsWith("delay-dead-letter")) {
                    Pattern pattern = Pattern.compile("delay-dead-letter ([0-9]+) (.*)", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(command);
                    if (matcher.matches()) {
                        Integer messagesExpires = Integer.parseInt(matcher.group(1));
                        String message = matcher.group(2);
                        String fullMessage = "[DELAYED] " + message + " - sent on " + new Date();
                        System.out.println("Sending message with delay: " + messagesExpires + "ms - " + message);

                        // get headers with name
                        Map<String, Object> headers = getDefaultHeaders();
                        // 1. build the amqp message properties out of the headers, see for "private-messages" publish how to do it

                        // 2. publish message
                        // channel.basicPublish ...
                    } else {
                        System.out.println("Unknown command.");
                    }
                } else {
                    System.out.println("Unknown command.");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static Map<String, Object> getDefaultHeaders() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("chat-username", NAME);
        return headers;
    }
}
