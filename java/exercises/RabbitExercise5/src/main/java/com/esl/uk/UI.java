package com.esl.uk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.GroupLayout.*;
import static javax.swing.GroupLayout.Alignment.*;
import static javax.swing.LayoutStyle.ComponentPlacement.*;

public class UI extends JFrame {

    private final String subscribe_to_queue;
    private final String send_to_queue;

    JTextArea entryTextArea = new JTextArea();
    JTextArea messagesSendTextArea = new JTextArea();
    JTextArea messagesRecvTextArea = new JTextArea();

    private final Logger logger = LoggerFactory.getLogger(UI.class);

    @SuppressWarnings("unused")
    public UI(String title, String sendToQueue, String subscribeToQueue) {

        logger.info("title ='{}', sendToQueue ='{}' subscribeToQueue ='{}'", title, sendToQueue, subscribeToQueue);

        send_to_queue = sendToQueue;
        subscribe_to_queue = subscribeToQueue;

        Publisher publisher = new Publisher(messagesSendTextArea, send_to_queue);
        Receiver receiver = new Receiver(messagesRecvTextArea, subscribe_to_queue);
        initComponents(title, publisher);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents(String title, final Publisher publisher) {

        JButton cancelButton = new JButton();
        JButton sendButton = new JButton();
        JLabel jLabel1 = new JLabel();
        JLabel labelSent = new JLabel();
        JLabel labelReceived = new JLabel();
        JPanel titlePanel = new JPanel();
        JPanel jPanel2 = new JPanel();
        JScrollPane jScrollPane1 = new JScrollPane();
        JScrollPane sentPane = new JScrollPane();
        JScrollPane jScrollPane3 = new JScrollPane();


        sendButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        logger.debug("clicked send button");
                        publisher.send(entryTextArea.getText(), 1);
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        titlePanel.setName("RabbitMQ"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(title);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(titlePanel);
        titlePanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1, PREFERRED_SIZE, 225, PREFERRED_SIZE)
                                .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addContainerGap(DEFAULT_SIZE, Short.MAX_VALUE))
        );


        entryTextArea.setColumns(20);
        entryTextArea.setRows(5);
        jScrollPane1.setViewportView(entryTextArea);

        sendButton.setText("Send");

        cancelButton.setText("Clear");

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(TRAILING)
                                        .addComponent(jScrollPane1)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(sendButton, PREFERRED_SIZE, 122, PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(cancelButton, PREFERRED_SIZE, 122, PREFERRED_SIZE)))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, PREFERRED_SIZE, 130, PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(BASELINE)
                                        .addComponent(sendButton, DEFAULT_SIZE, 41, Short.MAX_VALUE)
                                        .addComponent(cancelButton, DEFAULT_SIZE, 41, Short.MAX_VALUE))
                                .addContainerGap())
        );

        messagesSendTextArea.setBackground(new java.awt.Color(234, 232, 232));
        messagesSendTextArea.setColumns(20);
        messagesSendTextArea.setRows(5);
        sentPane.setViewportView(messagesSendTextArea);

//        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        labelSent.setHorizontalAlignment(SwingConstants.LEFT);
        labelSent.setText("Sent...");
        labelReceived.setHorizontalAlignment(SwingConstants.LEFT);
        labelReceived.setText("Received...");

//        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
//        jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
//        jLabel3.setText("Received messages");

        messagesRecvTextArea.setBackground(new java.awt.Color(234, 232, 232));
        messagesRecvTextArea.setColumns(20);
        messagesRecvTextArea.setRows(5);
        jScrollPane3.setViewportView(messagesRecvTextArea);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        GroupLayout.ParallelGroup parallelGroup = layout.createParallelGroup(LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(LEADING)
                                .addGroup(TRAILING, layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(layout.createParallelGroup(TRAILING)
                                                .addComponent(jPanel2, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(titlePanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(TRAILING)
                                                .addComponent(jScrollPane3, PREFERRED_SIZE, 252, PREFERRED_SIZE)
                                                .addGroup(


                                                        layout.createParallelGroup(LEADING)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addGap(82, 82, 82)
                                                                        .addComponent(labelSent, PREFERRED_SIZE, 105, PREFERRED_SIZE))

                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addGap(82, 82, 82)
                                                                        .addComponent(labelReceived, PREFERRED_SIZE, 105, PREFERRED_SIZE))

                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addGap(14, 14, 14)
                                                                        .addComponent(sentPane, PREFERRED_SIZE, 252, PREFERRED_SIZE)))

                                        )
                                        .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap());

        layout.setHorizontalGroup(parallelGroup);
        layout.setVerticalGroup(
                layout.createParallelGroup(LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(titlePanel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                                .addPreferredGap(UNRELATED)
                                .addComponent(jPanel2, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
//                                .addGap(18, 18, 18)
                                .addComponent(labelSent)
                                .addPreferredGap(UNRELATED)
                                .addComponent(sentPane, PREFERRED_SIZE, 147, PREFERRED_SIZE)
                                .addPreferredGap(UNRELATED)
//                                .addGap(18, 18, 18)
                                .addComponent(labelReceived)
                                .addPreferredGap(UNRELATED)
                                .addComponent(jScrollPane3, DEFAULT_SIZE, 155, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }// </edito

}
