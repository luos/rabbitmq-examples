using System;
using System.Text;
using System.Threading;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;

namespace RabbitExcercise1
{
    class Program
    {
        public static string YOUR_NAME = "FILL-IN-YOUR-NAME-HERE";
        public static TimeSpan twoMs = new TimeSpan(0, 0,0,0,1);

        static void Main(string[] args)
        {
            var factory =
                new ConnectionFactory()
                {
                    HostName = "18.159.212.120",
                    UserName = "",
                    Password = "" 
                };
            using (var connection = factory.CreateConnection())
            {
                using (var channel = connection.CreateModel())
                {
                    string queueName = YOUR_NAME + "-queue";
                    string routingKey = "myKey";
                    
                  
                    channel.QueueDeclare(queue: queueName, 
                        durable: true, 
                        exclusive: false, 
                        autoDelete: false);
                  

                    // 1. enable publish confirm
                    channel.ConfirmSelect();

                    // we create a persistent message
                    IBasicProperties props = channel.CreateBasicProperties();
                    props.DeliveryMode = 2;
                    
                    for (int i = 0; i < 10; i++){
                        string message = "Hello World " + (i.ToString()) + "! Published on " 
                            + DateTime.Now.ToString();
                        byte[] messageBody = Encoding.UTF8.GetBytes(message);
                        // we publish a message to the default exchange, using queue name as routing key
                        channel.BasicPublish(exchange: "", routingKey: queueName, body: messageBody, basicProperties: props);    
                        Console.WriteLine($" Published message " + i.ToString());
                    }

                    bool timedOut = false;
                    do {
                        Console.WriteLine("We are waiting for outstanding messages...");
                        channel.WaitForConfirms(twoMs, out timedOut);
                    }
                    while(timedOut); 
                    
                }
                Console.WriteLine("Connection closed.");    
            }
        }
            
    }
}
