package com.esl.uk;

/**
 *
 * Exercise 4 : Producers and Consumers
 *
 */
public class App 
{
    public static void main(String[] args)
    {
        try{
            // Create a Producer
            Producer producer = new Producer();

            // Wait for producer to create fabric
            Thread.sleep(100);

            // Create a Consumer
            Consumer consumer = new Consumer();

            producer.start(100);
            consumer.go(Rabbit.QUEUE, false);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
