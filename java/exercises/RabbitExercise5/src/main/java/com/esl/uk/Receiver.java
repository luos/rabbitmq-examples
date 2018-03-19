package com.esl.uk;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class Receiver extends SwingWorker< Integer, String >{

    private Connection connection;
    private Channel channel;
    private Logger logger;
    private JTextArea message_inbox;

    public Receiver(JTextArea message_inbox){
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            this.logger = LoggerFactory.getLogger(DefaultConsumer.class);

            // Set 'Connection' Credentials
            factory.setUsername("guest");
            factory.setPassword("guest");

            factory.setHost("localhost");
            factory.setPort(5672);

            this.logger.info("Setting up receiver");

            // Create 'Connection'
            this.connection = factory.newConnection();
            this.channel    = this.connection.createChannel();
            this.message_inbox = message_inbox;

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Integer doInBackground(){
        return 0;
    }

    public void go(String Queue, Boolean AutoAck){
        try {
            channel.basicConsume(Queue, AutoAck, new DefaultConsumer(channel) {
                //@Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body);
                    publish(message);
//                    message_inbox.append("RECEIVED Message: " + new String(body) + "\n");
//                    JOptionPane.showMessageDialog(null, "RECEIVED Message: " + message,
//                            "RECEIVED MESSAGE", JOptionPane.PLAIN_MESSAGE);
                    this.getChannel().basicAck(envelope.getDeliveryTag(), false);
//                    System.out.println(" [x] Received '" + message + "'");
//                    message_inbox.validate();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void process(List< String > publishedMessages){
        for (int i = 0; i < publishedMessages.size(); i++)
            message_inbox.append( publishedMessages.get(i) );
    }
}
