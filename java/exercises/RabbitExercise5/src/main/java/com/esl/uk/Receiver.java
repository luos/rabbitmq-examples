package com.esl.uk;

import com.rabbitmq.client.*;
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

    public Receiver(JTextArea message_inbox, String subscribe_to_queue){
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            this.logger = LoggerFactory.getLogger(DefaultConsumer.class);

            // Set 'Connection' Credentials
            factory.setUsername("guest");
            factory.setPassword("guest");

            factory.setHost("localhost");
            factory.setPort(5672);

            logger.info("Setting up receiver");

            // Create 'Connection'
            connection = factory.newConnection();
            channel    = this.connection.createChannel();

            this.go(subscribe_to_queue, true);
           this.message_inbox = message_inbox;

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Integer doInBackground(){
        return 0;
    }

    public void go(String queue, Boolean autoAck){
        try {
            channel.basicConsume(queue, autoAck, new DefaultConsumer(channel) {


                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body);
                    logger.info("received " + message);
                    publish(message);
//                    message_inbox.append("RECEIVED Message: " + new String(body) + "\n");
//                    JOptionPane.showMessageDialog(null, "RECEIVED Message: " + message,
//                            "RECEIVED MESSAGE", JOptionPane.PLAIN_MESSAGE);
//                    this.getChannel().basicAck(envelope.getDeliveryTag(), false);
//                    System.out.println(" [x] Received '" + message + "'");
//                    message_inbox.validate();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void process(List< String > publishedMessages){
        logger.info("processing " + publishedMessages);
        for (String s : publishedMessages) {
            message_inbox.append( "\n" + s );
        }
    }
}
