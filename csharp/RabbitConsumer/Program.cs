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
                    // we create a consumer which we can register a callback to
                    var consumer = new EventingBasicConsumer(channel);
                    // add the callback when message is received
                    consumer.Received += (ch, delivery) =>
                    {
                        // body is received as a byte array
                        var body = delivery.Body.ToArray();

                        var message = Encoding.ASCII.GetString(body);
                        Console
                            .WriteLine(" Received message: " + message);
                        
                        Thread.Sleep(1600); // simulate some work

                        // 2. we acknowledge the message so RabbitMQ removes it from the queue
                        channel.BasicAck(delivery.DeliveryTag, multiple: false);
                    };
                     

                    channel.BasicQos(prefetchSize: 0, prefetchCount: 3, global: true);
                    // subscribe to the queue
                    // the consumer is the EventingConsumer we created earlier
                    String consumerTag = channel.BasicConsume(queue: QUEUE_NAME, 
                                                    autoAck: false, 
                                                    consumer);


                    Console.WriteLine(" Press [enter] to exit.");
                    Console.ReadLine();   
                }
                
            }
 

        }
    }
}
