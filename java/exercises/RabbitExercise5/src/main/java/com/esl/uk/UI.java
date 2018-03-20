package com.esl.uk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI extends JFrame {

    private final Logger logger = LoggerFactory.getLogger(UI.class);

    public UI(String title, String sendToQueue, String subscribeToQueue) {
        logger.debug("title ='{}', sendToQueue ='{}' subscribeToQueue ='{}'", title, sendToQueue, subscribeToQueue);
        initComponents(title, subscribeToQueue, sendToQueue);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents(String title, String subscribe_to_queue, String send_to_queue) {

        this.setTitle(title);

        final JTextArea entryTextArea = new JTextArea();
        final JTextArea messagesSendTextArea = new JTextArea();
        final JTextArea messagesRecvTextArea = new JTextArea();

        final JButton sendButton = new JButton("Send");
        final JButton cancelButton = new JButton("Clear");
        final JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(cancelButton);
        buttonPanel.add(sendButton);

        final JLabel labelSent = new JLabel("Sent");
        final JPanel labelSentPanel = new JPanel();
        labelSentPanel.add(labelSent);


        final JLabel labelReceived = new JLabel("Received");
        final JPanel labelReceivedPanel = new JPanel();
        labelReceivedPanel.add(labelReceived);

        final JScrollPane entryTextPane = new JScrollPane();
        final JScrollPane sentPane = new JScrollPane();
        final JScrollPane receivedPane = new JScrollPane();

        final Publisher publisher = new Publisher(messagesSendTextArea, send_to_queue);
        new Receiver(messagesRecvTextArea, subscribe_to_queue);

        sendButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        logger.debug("clicked send button");
                        publisher.send(entryTextArea.getText());
                    }
                }
        );

        cancelButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        logger.debug("clicked clear button");
                        entryTextArea.setText("");
                    }
                }
        );

        entryTextArea.setColumns(20);
        entryTextArea.setRows(5);
        entryTextPane.setViewportView(entryTextArea);

        messagesSendTextArea.setColumns(20);
        messagesSendTextArea.setRows(5);
        sentPane.setViewportView(messagesSendTextArea);

        messagesRecvTextArea.setColumns(20);
        messagesRecvTextArea.setRows(5);
        receivedPane.setViewportView(messagesRecvTextArea);

        layout(buttonPanel, labelSentPanel, labelReceivedPanel, entryTextPane, sentPane, receivedPane);

    }

    private void layout(JPanel buttonPanel, JPanel labelSentPanel, JPanel labelReceivedPanel,
                        JScrollPane entryTextPane, JScrollPane sentPane, JScrollPane receivedPane) {
        getContentPane().setLayout(
                new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)
        );

        entryTextPane.setMinimumSize(new Dimension(100, 100));
        getContentPane().add(entryTextPane);
        getContentPane().add(buttonPanel);
        getContentPane().add(labelSentPanel);
        sentPane.setMinimumSize(new Dimension(100, 100));
        getContentPane().add(sentPane);
        getContentPane().add(labelReceivedPanel);
        receivedPane.setMinimumSize(new Dimension(100, 100));
        getContentPane().add(receivedPane);
        pack();
    }

}
