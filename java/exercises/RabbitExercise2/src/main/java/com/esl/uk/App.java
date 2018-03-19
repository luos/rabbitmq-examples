package com.esl.uk;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.slf4j.*;

/**
 * Exercise 2 : RabbitMQ Fanout Exchange
 *
 */
public class App
{
    final private static String QUEUE_1  = "EXERCISE_2_TEST_QUEUE_1";
    final private static String QUEUE_2  = "EXERCISE_2_TEST_QUEUE_2";
    final private static String QUEUE_3  = "EXERCISE_2_TEST_QUEUE_3";

    final private static String EXCHANGE = "EXERCISE_2_FANOUT_EXCHANGE";

    final private static String RK_1     = "EXERCISE_2_ROUTING_KEY_1";
    final private static String RK_2     = "EXERCISE_2_ROUTING_KEY_2";
    final private static String RK_3     = "EXERCISE_2_ROUTING_KEY_3";

    public static void main( String[] args )
    {
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            Logger logger = LoggerFactory.getLogger(App.class);
            logger.info( "Exercise 2 ..." );

            factory.setAutomaticRecoveryEnabled(true);

            // Set 'Connection' Credentials
            factory.setUsername("guest");
            factory.setPassword("guest");

            factory.setHost("localhost");
            factory.setPort(5672);

            logger.info( "Setting up producer ..." );

            // Create 'Connection'
            Connection connection  = factory.newConnection();
            Channel    channel     = connection.createChannel();

            // Create x3 Queues
            channel.queueDeclare(QUEUE_1, true, false, false, null);
            channel.queueDeclare(QUEUE_2, true, false, false, null);
            channel.queueDeclare(QUEUE_3, true, false, false, null);

            // Create 'exchange
            channel.exchangeDeclare(EXCHANGE, "fanout");

            // Bind Queues to Exchanges
            channel.queueBind(QUEUE_1, EXCHANGE, RK_1);
            channel.queueBind(QUEUE_2, EXCHANGE, RK_2);
            channel.queueBind(QUEUE_3, EXCHANGE, RK_3);

            logger.info( "Start publishing messages..." );

            // Publish 100 messages
            int n = 0;
            for( n = 0; n < 100; n ++ ){
                String message = "Message: " + n;
                channel.basicPublish(EXCHANGE, RK_1, null, message.getBytes());
            }

            logger.info( "Published {} messages ...", n );


        } catch (Exception e){
            e.printStackTrace();
        }
        System.exit(0);
    }
}
