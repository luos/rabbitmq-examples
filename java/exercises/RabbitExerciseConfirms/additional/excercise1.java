Connection consumerConnection = factory.newConnection();
Channel consumerChannel = connection.createChannel();
consumerChannel.basicConsume(QUEUE, new Consumer() {
    @Override
    public void handleConsumeOk(String consumerTag) {
        System.out.println("Consume started, tag: " + consumerTag);
    }

    @Override
    public void handleCancelOk(String consumerTag) {
        System.out.println("Consume successfully cancelled, tag: " + consumerTag);
    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
        System.out.println("Consume cancelled by RabbitMQ, tag: " + consumerTag);
    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        System.out.println("Shutting down: " + consumerTag + " " + sig.getReason());
    }

    @Override
    public void handleRecoverOk(String consumerTag) {
        
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("Received message: " + new String(body));
        consumerChannel.basicAck(envelope.getDeliveryTag(), false);
    }
});

Thread.sleep(10000);
System.out.println("Closing connections...");
consumerConnection.close(5000);
connection.close(5000);