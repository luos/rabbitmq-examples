package com.esl.uk;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class Receiver extends SwingWorker<Integer, String> {

    private Logger logger;
    private JTextArea message_inbox;

    public Receiver(JTextArea message_inbox, String subscribe_to_queue) {
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
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            this.go(channel, subscribe_to_queue, true);
            this.message_inbox = message_inbox;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer doInBackground() {
        return 0;
    }

    public void go(Channel channel, String queue, Boolean autoAck) {
        try {
            channel.basicConsume(queue, autoAck, new DefaultConsumer(channel) {
                @SuppressWarnings("RedundantThrows")
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {

                    String message =
                            String.format("RECV: %s \n", new String(body));

                    logger.info("received " + message);
                    publish(message);
                    //this.getChannel().basicAck(envelope.getDeliveryTag(), false);
                    //message_inbox.validate();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void process(List<String> publishedMessages) {
        logger.info("processing " + publishedMessages);
        for (String s : publishedMessages) {
            message_inbox.append(s);
        }
    }
}
