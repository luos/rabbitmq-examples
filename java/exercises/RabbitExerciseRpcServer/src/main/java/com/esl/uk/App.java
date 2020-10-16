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

            channel.basicQos(5, false);

            channel.addReturnListener((returned) -> {
                System.out.println("This message could not be routed, queue does not exist: " + returned.getRoutingKey() + " - " + new String(returned.getBody()));
            });

            String serviceRequestsExchange = NAME + "-service-requests";


            // 1. Declare the service requests exchange, it should be an x-random, durable
            // channel.exchangeDeclare(serviceRequestsExchange, type, durable);

            // 1. Declare the service queues, there should be 3 queues
            String[] serviceQueueNames = new String[]{
                    NAME + "-service-queue-1",
                    NAME + "-service-queue-2",
                    NAME + "-service-queue-3"
            };

            for (String queue : serviceQueueNames) {
                // 1. declare the queues and bind them to the serviceRequestsExchange
                // 2. these queues should be quorum queues
                // 2. 1. add to queue arguments the x-queue-type argument with the value quorum
                HashMap<String, Object> queueArguments = new HashMap<>();
                // queueArguments.put("x-queue-type", "quorum");

                // channel.queueDeclare(queue, durable=true, exclusive=false, autoDelete = false, queueArguments);

                // 3. bind the queue to the exchange, because it is a random exchange routing key does not matter
                // channel.queueBind(queue, exchange, routingKey);
            }

            for (String queue : serviceQueueNames) {
                // process incoming service requests
                channel.basicConsume(queue, new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body)
                            throws IOException {


                        // 1. get the reply to queue out of the properties
                        String replyToQueue = properties.getReplyTo();


                        String message = new String(body);
                        System.out.println("[" + queue + "] Received request: '" + message + "' through '" + queue + "' replying to: '" + replyToQueue + "'");

                        if (replyToQueue != null) {
                            // simulate work
                            sleep(3000);
                            // we reply to the queue after 5 seconds
                            String result = "Your request was processed: '" + message + "' on " + new Date();

                            // edit it here
                            // 1. Publish an anser to the queue
                            // this.getChannel().basicPublish(exchange, replyToQueue, true, null, result.getBytes());
                            // acknowledge the messages, delivery tag is from the envelope.getDeliveryTag()
                            // this.getChannel().basicAck(deliverTag, multiple= false);


                        } else {
                            System.out.println("There should be a reply to saying where to reply");
                            // 1. Reject the message with requeue=false
                            //this.getChannel().basicReject(deliveryTag, requeue=false);
                        }

                    }
                });

            }

            System.out.println("Waiting for requests...");
            sleep(600000);

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
