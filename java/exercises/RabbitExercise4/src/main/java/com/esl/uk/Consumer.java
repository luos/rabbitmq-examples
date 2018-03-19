package com.esl.uk;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Consumer {

    private Connection connection;
    private Channel channel;
    private Logger logger;

    public Consumer(){
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            this.logger = LoggerFactory.getLogger(DefaultConsumer.class);

            // Set 'Connection' Credentials
            factory.setUsername("guest");
            factory.setPassword("guest");

            factory.setHost("localhost");
            factory.setPort(5672);

            this.logger.info("Setting up producer ...");

            // Create 'Connection'
            this.connection = factory.newConnection();
            this.channel    = this.connection.createChannel();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void go(String Queue, Boolean AutoAck){
        try {
            this.channel.basicConsume(Queue, AutoAck, new DefaultConsumer(this.channel) {
                //@Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           BasicProperties properties, byte[] body) throws IOException {
                logger.info("RECEIVED Message: " + new String(body));
                this.getChannel().basicAck(envelope.getDeliveryTag(), false);
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
