package com.esl.uk;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer {
    private Connection connection;
    private Channel ch;
    private Logger logger;

    public Producer(){
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            this.logger = LoggerFactory.getLogger(DefaultConsumer.class);

            // Set 'Connection' Credentials
            factory.setUsername("guest");
            factory.setPassword("guest");

            factory.setHost("localhost");
            factory.setPort(5672);

            this.logger.info("Setting up consumer ...");

            // Create 'Connection'
            this.connection = factory.newConnection();
            this.ch = this.connection.createChannel();

            if (this.ch.isOpen())
                logger.info("Connection and Channel successfully opened");

            // Setup Fabric
            this.ch.exchangeDeclare(Rabbit.EXCHANGE, "direct");
            this.ch.queueDeclare(Rabbit.QUEUE, false, false, false, null);
            this.ch.queueBind(Rabbit.QUEUE, Rabbit.EXCHANGE, Rabbit.RK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(int numMessages){
        String message = "Messages published on Routing Key: ";

        for(int i = 0; i < numMessages; i++){
            try {
                if(this.ch.isOpen())
                    this.ch.basicPublish(Rabbit.EXCHANGE, Rabbit.RK, null, message.concat(Rabbit.RK).getBytes());
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        this.logger.info("Published {} messages on routing key: {}", numMessages, Rabbit.RK);
    }
}
