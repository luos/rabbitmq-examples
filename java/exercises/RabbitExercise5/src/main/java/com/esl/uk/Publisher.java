package com.esl.uk;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;

public class Publisher {
    private Connection connection;
    private Channel ch;
    private Logger logger;
    private JTextArea message_outbox;

    public Publisher(JTextArea message_outbox_device, String DestinationQueue) {
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            this.logger = LoggerFactory.getLogger(DefaultConsumer.class);

            // Set 'Connection' Credentials
            factory.setUsername("guest");
            factory.setPassword("guest");

            factory.setHost("localhost");
            factory.setPort(5672);

            this.logger.info("Setting up message publisher");

            // Create 'Connection'
            this.connection = factory.newConnection();
            this.ch = this.connection.createChannel();

            if (this.ch.isOpen())
                logger.info("Connection and Channel open!");
            // Setup Fabric
            this.ch.exchangeDeclare(Rabbit.EXCHANGE, "direct");
            this.ch.queueDeclare(DestinationQueue, false, false, false, null);
            this.ch.queueBind(DestinationQueue, Rabbit.EXCHANGE, Rabbit.RK);
            this.message_outbox = message_outbox_device;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String message, int numMessages){
        for( int i = 0; i < numMessages; i++ ){
            try {
              if(this.ch.isOpen() && message.equals("") == false) {
                  this.ch.basicPublish(Rabbit.EXCHANGE, Rabbit.RK, null, message.concat(Rabbit.RK).getBytes());
                    message_outbox.append("SENT: " + message.concat(Rabbit.RK) + "\n");
              }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
//        this.logger.info( "Published {} messages on routing key: {}\n", numMessages, Rabbit.RK);
    }

}
