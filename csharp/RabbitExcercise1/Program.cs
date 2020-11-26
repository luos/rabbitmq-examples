using System;
using System.Text;
using RabbitMQ.Client;
using System.Threading;

namespace RabbitExcercise1
{
    class Program
    {
        public static string YOUR_NAME = "REPLACE_THIS_WITH_YOUR_NAME";

        static void Main(string[] args)
        {
            var factory = new ConnectionFactory() { 
                HostName = "localhost",
                UserName = "guest",
                Password = "guest"
            };
            using (var connection = factory.CreateConnection())
            {
                using (var channel = connection.CreateModel())
                {
                    string queueName = YOUR_NAME + "-queue";
                    string message = "Hello World! Published on " + DateTime.Now.ToString();
                    byte[] messageBody = Encoding.UTF8.GetBytes(message);
                    int deliveryMode = 2;

                    // 1. declare the queue, it should be durable
                    // channel.QueueDeclare(queue: string, durable: bool, exclusive:bool=false, autoDelete:bool=false)
                    
                    
                    // 2. publish a message to the empty string named exchange, with routing key of queue name
                    // channel.basicPublish(exchange: string, routingKey: string, body: byte[])
                
                    // sleep so the message get to the server before closing the channel 
                    // and connection
                    Console.WriteLine($" Published message '{message}' to {queueName}");
                }
                
                Thread.Sleep(500);
            }


            Console.WriteLine(" Press [enter] to exit.");
            Console.ReadLine();
        }
    }
}
