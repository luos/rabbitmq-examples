package com.esl.uk;

import java.util.Map;
import java.util.HashMap;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.slf4j.*;

/**
 *
 * Exercise 7 : Dead lettering
 *
 */
public class App
{
    final private static String QUEUE_1  = "EXERCISE_7_TEST_DEFAULT_QUEUE";
    final private static String QUEUE_2  = "EXERCISE_7_TEST_DEAD_LETTER_QUEUE";

    final private static String EXCHANGE    = "EXERCISE_7_DIRECT_EXCHANGE";
    final private static String EXCHANGE_DL = "EXERCISE_7_DIRECT_DEAD_LETTER_EXCHANGE";

    final private static String RK_1 = "EXERCISE_7_RK_1";
    final private static String RK_2 = "EXERCISE_7_RK_2";

    final private static int ttl_ms = 30000;

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

            logger.info("Setting up producer ...");

            // Create Connection and Channel
            Connection connection  = factory.newConnection();
            Channel    channel     = connection.createChannel();

            // Create Direct Exchanges
            channel.exchangeDeclare(EXCHANGE,    "direct");
            channel.exchangeDeclare(EXCHANGE_DL, "direct");

            // Define Lazy Queue arguments
            Map declare_args = new HashMap();
            declare_args.put("x-dead-letter-exchange", EXCHANGE_DL);
            // If routing is not set, original message's RK will be used instead!
            declare_args.put("x-dead-letter-routing-key", RK_2);
            // Setting message TTL expiry
            declare_args.put("x-message-ttl", ttl_ms);

            // Create x5 Queues
            channel.queueDeclare(QUEUE_1, true, false, false, declare_args);
            channel.queueDeclare(QUEUE_2, true, false, false, null);

            // Create Bindings
            channel.queueBind(QUEUE_1, EXCHANGE,    RK_1);
            channel.queueBind(QUEUE_2, EXCHANGE_DL, RK_2);

            logger.info( "Start publishing messages..." );

            // Publish 100 messages to default queue with expiring messages
            publish(logger, channel, EXCHANGE, RK_1, 100);

            /*
             *
             * i. Before 'TTL EXPIRY':
             *
             *    EXERCISE_7_TEST_DEFAULT_QUEUE	     100
             *    EXERCISE_7_TEST_DEAD_LETTER_QUEUE  0
             *
             * ii. After 'TTL EXPIRY':
             *
             *    EXERCISE_7_TEST_DEFAULT_QUEUE	     0
             *    EXERCISE_7_TEST_DEAD_LETTER_QUEUE  100
             *
             */

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
