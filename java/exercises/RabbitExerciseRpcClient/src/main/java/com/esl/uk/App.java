package com.esl.uk;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQBasicProperties;
import org.slf4j.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Exercise 1 : RabbitMQ Confirm Exercise
 */
public class App {
    final private static String NAME = "PUT_YOUR_NAME_HERE";

    public static void main(String[] args) throws Exception {
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
                System.out.println("This message could not be routed, queue does not exist: " + returned.getRoutingKey() + " - " + new String(returned.getBody()));
            });

            String serviceRequestsExchange = NAME + "-service-requests";

            // 1. Declare the same exchange as for the RPCServer

            ConcurrentLinkedQueue<String> responses = new ConcurrentLinkedQueue<String>();

            // process incoming service requests
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body)
                        throws IOException {
                    String response = new String(body);
                    System.out.println("[Consumer] Received response: " + response);
                    responses.add(response);

                    // 1. Acknowledge the message
                    // this.getChannel().basicAck(...)
                }
            };


            while (true) {
                String queueName = NAME + "-response-" + UUID.randomUUID();
                String request = NAME + " " + new Random().nextInt();
                System.out.println("Sending request '" + request + "' reply to: " + queueName);


                // 1. declare a random named queue which we use for responses for rpc requests
                /// channel.queueDeclare(..., false, false, null);

                // 2. consume from the queue
                // channel.basicConsume(queueName, consumer);

                /// 3. build the message properties with replyTo set to the random queue name
                

                // 4. Publish the message
                // channel.basicPublish(exchange,routingKey,mandatory=true, props, request.getBytes());

                String response;
                do {
                    System.out.println("Waiting for response...");
                    response = responses.poll();
                    sleep(2000);
                } while (response == null);

                // 5. Delete the queue channel.queueDelete(queueName)


                System.out.println("Received response: " + response);
                sleep(5000);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            System.out.println("Sleep interrupted.s");
        }
    }

    private static Map<String, Object> getDefaultHeaders() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("chat-username", NAME);
        return headers;
    }
}
