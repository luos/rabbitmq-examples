package com.esl.uk;

/**
 *
 * Exercise 8 : Prefetch
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
            Consumer consumer_1 = new Consumer("CONSUMER_1",100);
            Consumer consumer_2 = new Consumer("CONSUMER_2",50);
            Consumer consumer_3 = new Consumer("CONSUMER_3",10);

            producer.start(100);

            consumer_1.go(Helper.QUEUE_1, false);
            consumer_2.go(Helper.QUEUE_2, false);
            consumer_3.go(Helper.QUEUE_3, false);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
