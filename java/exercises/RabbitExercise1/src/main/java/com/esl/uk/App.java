package com.esl.uk;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.slf4j.*;

/**
 * Exercise 1 : RabbitMQ Direct Exchange
 *
 */
public class App
{
    final private static String QUEUE    = "EXERCISE_1_TEST_QUEUE";
    final private static String EXCHANGE = "EXERCISE_1_DIRECT_EXCHANGE";
    final private static String RK       = "EXERCISE_1_ROUTING_KEY";

    public static void main( String[] args )
    {
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            Logger logger = LoggerFactory.getLogger(App.class);

            factory.setAutomaticRecoveryEnabled(true);

            // Set 'Connection' Credentials
            factory.setUsername("guest");
            factory.setPassword("guest");

            factory.setHost("localhost");
            factory.setPort(5672);

            logger.info( "Setting up producer ..." );

            // Create Connection and
            Connection connection  = factory.newConnection();
            Channel    channel     = connection.createChannel();

            // Create Queue
            channel.queueDeclare(QUEUE, true, false, false, null);

            // Create Exchange
            channel.exchangeDeclare(EXCHANGE, "direct");

            // Bind Queue to Exchange
            channel.queueBind(QUEUE, EXCHANGE, RK);

            logger.info( "Start publishing messages..." );

            // Publish 100 messages
            int n = 0;
            for( n = 0; n < 10; n ++ ){
                String message = "Published message: " + n;
                channel.basicPublish(EXCHANGE, RK, null, message.getBytes());
            }

            logger.info( "Completed publishing {} messages ...", n );


        } catch (Exception e){
            e.printStackTrace();
        }
        System.exit(0);
    }
}
