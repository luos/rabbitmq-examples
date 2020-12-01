using System;
using System.Text;
using System.Threading;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;

namespace RabbitExcercise1
{
    class Program
    {
        public static string YOUR_NAME = "Lajos-x-";

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
                    string message =
                        "Hello World! Published on " + DateTime.Now.ToString();
                    byte[] messageBody = Encoding.UTF8.GetBytes(message);
                    int deliveryMode = 2;

                    // 1. declare two queues, add a policy on the GUI for one of them to make it lazy
                    channel.QueueDeclare(queue: queueName, 
                        durable: true, 
                        exclusive: false, 
                        autoDelete: false);
                    
                    
                    // 2. declare a fanout exchange
                    string exchangeName = YOUR_NAME + "-exchange";
                    channel.ExchangeDeclare(exchange: exchangeName, type: "direct");

                    // bind the exchange and both queues
                    channel.QueueBind(queue: queueName, exchange: exchangeName, routingKey: routingKey);
                    
                    // enable confirms
                    channel.ConfirmSelect();
                    // publish a message to the  exchange, with routing key of "myKey"
                    for (int i = 0; i < 200; i++)
                    {
                        channel.BasicPublish(exchange: exchangeName, routingKey: routingKey, body: messageBody);    
                    }

                    // wait for confirms
                    channel.WaitForConfirms();

                    Console
                        .WriteLine($" Published messages with routing key {routingKey} to exchange '{exchangeName}'");

                }
                
            }

        
            Console.WriteLine(" Press [enter] to exit.");
            Console.ReadLine();
        }
    }
}
