using System;
using System.Text;
using System.Threading;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;

namespace RabbitExcercise1
{
    class Program
    {
        public static string YOUR_NAME = "YOUR-NAME";
        public static TimeSpan twoMs = new TimeSpan(0, 0,0,0,20);

        static void Main(string[] args)
        {
            var factory =
                new ConnectionFactory()
                {
                    HostName = "18.159.212.120",
                    UserName = "testuser",
                    Password = "7HDpJ23ntECiR6bx"
                };
            using (var connection = factory.CreateConnection())
            {
                using (var channel = connection.CreateModel())
                {
                    string queueName = YOUR_NAME + "-queue";
                    string routingKey = "myKey";
                    string message =
                        "Hello World! Published on " + DateTime.Now.ToString();
                    byte[] messageBody = Encoding.UTF8.GetBytes(message);
                    int deliveryMode = 2;

                    // 1. declare two queues, add a policy on the GUI for one of them to make it lazy
                    // 1.a Optionally use queue arguments to make the queue lazy
                    // Dictionary<string, object> args = new Dictionary<string, object>()
                    // {
                    //    { "x-queue-mode", "lazy" },
                    //    { "x-queue-type", "classic"} // this can be quorum in a cluster
                    // };            


                    channel.QueueDeclare(queue: queueName, 
                        durable: true, 
                        exclusive: false, 
                        autoDelete: false);
                    // 2. declare second queue
                    
                    // 2. declare an exchange
                    string exchangeName = YOUR_NAME + "-exchange";
                    // channel.ExchangeDeclare(exchange: exchangeName, type: "direct");

                    // 3. bind the exchange and both queues
                    // channel.QueueBind(queue: queueName, exchange: exchangeName, routingKey: routingKey);
                    // channel.QueueBind(queue: queueName, exchange: exchangeName, routingKey: routingKey);
                    
                    // 4. enable confirms
                    channel.ConfirmSelect();

                    // 5. we want to publish durable messages
                    IBasicProperties props = channel.CreateBasicProperties();
                    // props.DeliveryMode = 2;

                    // publish a message to the  exchange so that it goes into both queues
                    for (int i = 0; i < 200; i++)
                    {
                        channel.BasicPublish(exchange: exchangeName, 
                            routingKey: routingKey, 
                            body: messageBody,
                            basicProperties: props
                            );    
                    }

                    bool timedOut = false;
                    do {
                        Console.WriteLine("We are waiting for outstanding messages...");
                        channel.WaitForConfirms(twoMs, out timedOut);
                    }
                    while(timedOut); 

                    Console
                        .WriteLine($" Published messages with routing key {routingKey} to exchange '{exchangeName}'");

                }
                
            }

        
            Console.WriteLine(" Press [enter] to exit.");
            Console.ReadLine();
        }
    }
}
