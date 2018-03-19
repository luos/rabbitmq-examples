package com.esl.uk;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.slf4j.*;

/**
 *
 * Exercise 3 : RabbitMQ Topic Exchange
 *
 */
public class App
{
    final private static String QUEUE_1  = "EXERCISE_3_TEST_QUEUE_1";
    final private static String QUEUE_2  = "EXERCISE_3_TEST_QUEUE_2";
    final private static String QUEUE_3  = "EXERCISE_3_TEST_QUEUE_3";
    final private static String QUEUE_4  = "EXERCISE_3_TEST_QUEUE_4";
    final private static String QUEUE_5  = "EXERCISE_3_TEST_QUEUE_5";

    final private static String EXCHANGE = "EXERCISE_3_TOPIC_EXCHANGE";

    final private static String MATCH_SPEC_1 = "*.EVENT";
    final private static String MATCH_SPEC_2 = "*.LOG";
    final private static String MATCH_SPEC_3 = "*.REQUEST";
    final private static String MATCH_SPEC_4 = "SERVICE.*";
    final private static String MATCH_SPEC_5 = "#";

    final private static String RK_EVENT     = "EXERCISE_3.EVENT";
    final private static String RK_LOG       = "EXERCISE_3.LOG";
    final private static String RK_REQUEST   = "EXERCISE_3.REQUEST";
    final private static String RK_SERVICE   = "SERVICE.EXERCISE_3_RK";
    final private static String RK_UNMATCHED = "EXERCISE_3_UNMATCHED";

    public static void main( String[] args )
    {
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            Logger logger = LoggerFactory.getLogger(App.class);

            factory.setAutomaticRecoveryEnabled(true);

            // Set Connection Credentials
            factory.setUsername("guest");
            factory.setPassword("guest");

            factory.setHost("localhost");
            factory.setPort(5672);

            System.out.println( "Setting up producer ..." );

            // Create Connection and Channel
            Connection connection  = factory.newConnection();
            Channel    channel     = connection.createChannel();

            // Create x5 Queues
            channel.queueDeclare(QUEUE_1, true, false, false, null);
            channel.queueDeclare(QUEUE_2, true, false, false, null);
            channel.queueDeclare(QUEUE_3, true, false, false, null);
            channel.queueDeclare(QUEUE_4, true, false, false, null);
            channel.queueDeclare(QUEUE_5, true, false, false, null);

            // Create Topic Exchange
            channel.exchangeDeclare(EXCHANGE, "topic");

            // Create Bindings
            channel.queueBind(QUEUE_1, EXCHANGE, MATCH_SPEC_1);
            channel.queueBind(QUEUE_2, EXCHANGE, MATCH_SPEC_2);
            channel.queueBind(QUEUE_3, EXCHANGE, MATCH_SPEC_3);
            channel.queueBind(QUEUE_4, EXCHANGE, MATCH_SPEC_4);
            channel.queueBind(QUEUE_5, EXCHANGE, MATCH_SPEC_5);

            logger.info( "Start publishing messages..." );

            // Publish 100 Messages to each exchange
            publish(logger, channel, EXCHANGE, RK_EVENT,     100);
            publish(logger, channel, EXCHANGE, RK_LOG,       100);
            publish(logger, channel, EXCHANGE, RK_REQUEST,   100);
            publish(logger, channel, EXCHANGE, RK_SERVICE,   100);
            publish(logger, channel, EXCHANGE, RK_UNMATCHED, 100);

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
