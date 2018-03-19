package com.esl.uk;

import com.sun.tools.internal.ws.wsdl.document.jaxws.Exception;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * Application
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        final UI ui_1 = new UI("Chat Client 1", Rabbit.CHAT_CLIENT_QUEUE_1, Rabbit.CHAT_CLIENT_QUEUE_2);
        final UI ui_2 = new UI("Chat Client 2", Rabbit.CHAT_CLIENT_QUEUE_2, Rabbit.CHAT_CLIENT_QUEUE_1);

        final Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();

        ui_1.setLocation(screen_dim.width/4 - ui_1.getSize().width/2,
                         screen_dim.height/2 - ui_1.getSize().height/2);
        ui_2.setLocation((3 * screen_dim.width/4) - ui_2.getSize().width/2,
                         screen_dim.height/2 - ui_2.getSize().height/2);
        start(ui_1);
        start(ui_2);
    }

    private static void start(final UI ui){
//        try{
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    ui.setVisible(true);
                }
            });
//        }catch(InvocationTargetException e){
//            e.printStackTrace();
//        }catch(InterruptedException e){
//            e.printStackTrace();
//        }

    }
}
