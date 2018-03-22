package com.esl.uk;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Publisher {

    private String client_id;
    private Channel channel;
    private Logger logger;
    private String RK;
    private JTextArea messageOutTextArea;

    public Publisher(JTextArea messageOutTextArea, String id, String destinationQueue, String rk) {
        this.messageOutTextArea = messageOutTextArea;
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
            factory.setAutomaticRecoveryEnabled(true);
            factory.setTopologyRecoveryEnabled(true);
            Connection connection
                    = factory.newConnection();
            this.channel = connection.createChannel();

            this.client_id = id;
            this.RK = rk;

            if (channel.isOpen()) {
                logger.info("Connection and Channel open!");
            }
            // Setup Fabric
            channel.exchangeDeclare(Rabbit.EXCHANGE, "direct");
            channel.queueDeclare(destinationQueue, false, false, false, null);
            channel.queueBind(destinationQueue, Rabbit.EXCHANGE, this.RK);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
            try {
                if (this.channel.isOpen() && (!message.isEmpty())) {
                    this.channel.basicPublish(Rabbit.EXCHANGE, RK, null, message.getBytes());
                    logger.info(client_id + " published message: " + message + ", to RK: " + RK);
                    messageOutTextArea.append(String.format("SENT: %s \n", message));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
