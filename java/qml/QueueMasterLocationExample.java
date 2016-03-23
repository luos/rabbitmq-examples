import java.util.*;
import java.io.IOException;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

public class QueueMasterLocationExample {

  private static final String QUEUE = "microservice.queue.1";

  public static void main(String[] argv)
                      throws java.io.IOException {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    // set queue master location strategy
    Map args = new HashMap(); 
    args.put("x-queue-master-locator", "min-masters");
	
    // declare queue
    channel.queueDeclare(QUEUE, false, false, false, args);

	// send a test message from the command line
    String message = getMessage(argv);

    channel.basicPublish( "", QUEUE,
            MessageProperties.PERSISTENT_TEXT_PLAIN,
            message.getBytes());
			
    System.out.println(" [x] Sent '" + message + "'");

    channel.close();
    connection.close();
  }      
}