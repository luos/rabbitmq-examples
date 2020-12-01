using System;
using System.Text;
using System.Threading;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;

namespace RabbitExcercise1
{
    class Program
    {
        public static string QUEUE_NAME = "FILL-IN-YOUR-NAME-HERE-queue";

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
                    string queueName = QUEUE_NAME + "-queue";
                    string routingKey = "myKey";
                    for (int i = 0; i < 200; i++)
                    {
                        string message =
                            "Hello World!  " +
                            i.ToString() +
                            " Published on " +
                            DateTime.Now.ToString();
                        byte[] messageBody = Encoding.UTF8.GetBytes(message);

                
                         channel
                            .BasicPublish(exchange: "",
                            routingKey: QUEUE_NAME,
                            body: messageBody);

                        Console
                            .WriteLine($" Published message '{message}' to queue {QUEUE_NAME}");

                        Thread.Sleep(1500);
                    }
                }
            } 

            Console.WriteLine(" Press [enter] to exit.");
            Console.ReadLine();
        }
    }
}
