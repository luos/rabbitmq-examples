using System;
using System.Text;
using System.Threading;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;

namespace RabbitExcercise1
{
    class Program
    {
        public static string YOUR_NAME = "REPLACE_THIS_WITH_YOUR_NAME";

        static void Main(string[] args)
        {
            var factory =
                new ConnectionFactory()
                {
                    HostName = "localhost",
                    UserName = "guest",
                    Password = "guest"
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

                    // 1. declare the queue, it should be durable
                    // channel.QueueDeclare(queue: string, durable: bool, exclusive:bool=false, autoDelete:bool=false)
                    // 2. declare an exchange
                    // channel.ExchangeDeclare(exchange: string, type: string)
                    string exchangeName = YOUR_NAME + "-exchange";
                    

                    // 3. bind the exchange and queue
                    // channel.QueueBind(queue: string, exchange: string, routingKey: string)
                    
                    // 3. publish a message to the  exchange, with routing key of "myKey"
                    // channel.basicPublish(exchange: string, routingKey: string, body: byte[])
                


                    Console
                        .WriteLine($" Published message '{message}' to with routing key {routingKey} to exchange '{exchangeName}'");

                    // sleep so the message get to the server before closing the channel
                    // and connection
                    Thread.Sleep(500);
                }
                
            }

            // ---------------------------
            // second exercise

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

                        // 2. we acknowledge the message so RabbitMQ removes it from the queue
                        // channel.BasicAck(delivery.DeliveryTag, multiple: boolean = false);
                    };
                    
                    string queueName = YOUR_NAME + "-queue";

                    // 1. subscribe to the queue
                    // the consumer is the EventingConsumer we created earlier
                    // String consumerTag = channel.BasicConsume(queue: string, autoAck: false, consumer);

                    Thread.Sleep(1500);
                }
                
            }

            Thread.Sleep(500);
            Console.WriteLine(" Press [enter] to exit.");
            Console.ReadLine();
        }
    }
}
