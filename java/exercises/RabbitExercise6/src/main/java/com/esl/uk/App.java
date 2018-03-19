package com.esl.uk;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.slf4j.*;

/**
 *
 * Exercise 6 : Lazy Queues
 *
 */
public class App
{
    final private static String QUEUE_1  = "EXERCISE_6_TEST_DEFAULT_QUEUE_1";
    final private static String QUEUE_2  = "EXERCISE_6_TEST_LAZY_QUEUE_2";

    final private static String EXCHANGE = "EXERCISE_6_DIRECT_EXCHANGE";

    final private static String RK_1 = "EXERCISE_6_RK_1";
    final private static String RK_2 = "EXERCISE_6_RK_2";

    public static void main( String[] args ) {

        try {
            final ConnectionFactory factory = new ConnectionFactory();
            Logger logger = LoggerFactory.getLogger(App.class);

            factory.setAutomaticRecoveryEnabled(true);

            // Set Connection Credentials
            factory.setUsername("guest");
            factory.setPassword("guest");

            factory.setHost("localhost");
            factory.setPort(5672);

            logger.info("Setting up producer ...");

            // Create Connection and Channel
            Connection connection = factory.newConnection();
            Channel    channel     = connection.createChannel();

            // Define Lazy Queue arguments
            Map declare_args = new HashMap();
            declare_args.put("x-queue-mode", "lazy");

            // Create x5 Queues
            channel.queueDeclare(QUEUE_1, true, false, false, null);
            channel.queueDeclare(QUEUE_2, true, false, false, declare_args);

            // Create Topic Exchange
            channel.exchangeDeclare(EXCHANGE, "direct");

            // Create Bindings
            channel.queueBind(QUEUE_1, EXCHANGE, RK_1);
            channel.queueBind(QUEUE_2, EXCHANGE, RK_2);

            logger.info( "Start publishing messages..." );

            // Publish 1000 messages to both queues
            publish(logger, channel, EXCHANGE, RK_1, 1000);
            publish(logger, channel, EXCHANGE, RK_2, 1000);

            // Compare memory utilization with 'rabbitmqctl list_queues name memory'

            // EXERCISE_6_TEST_DEFAULT_QUEUE_1	1804352
            // EXERCISE_6_TEST_LAZY_QUEUE_2	    89408
            connection.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void publish(Logger logger, Channel channel, String EXCHANGE, String RK, int n){
        for( int i = 0; i < n; i++ ){
            String message = "Messages published on Routing Key: ";
            try {
                channel.basicPublish(EXCHANGE, RK, null, message.concat(RK).getBytes());
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        logger.info( "Published {} messages on routing key: {}", n, RK);
    }
}
