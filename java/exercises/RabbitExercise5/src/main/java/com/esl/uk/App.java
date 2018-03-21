package com.esl.uk;

import java.awt.*;
import java.util.Properties;

/**
 * Application
 */
public class App {
    public static void main(String[] args) {

        Properties props = System.getProperties();
        props.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

        final UI ui_1 = new UI("Client 1|Send q1|Recv q2 ", Rabbit.CHAT_CLIENT_QUEUE_1, Rabbit.CHAT_CLIENT_QUEUE_2);
        final UI ui_2 = new UI("Client 2|Send q2|Recv q1 ", Rabbit.CHAT_CLIENT_QUEUE_2, Rabbit.CHAT_CLIENT_QUEUE_1);

        final Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();

        ui_1.setLocation(screen_dim.width / 4 - ui_1.getSize().width / 2,
                screen_dim.height / 2 - ui_1.getSize().height / 2);
        ui_2.setLocation((3 * screen_dim.width / 4) - ui_2.getSize().width / 2,
                screen_dim.height / 2 - ui_2.getSize().height / 2);
        start(ui_1);
        start(ui_2);
    }

    private static void start(final UI ui) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ui.setVisible(true);
            }
        });
    }
}
