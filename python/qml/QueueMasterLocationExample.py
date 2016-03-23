#!/usr/bin/env python

from pika.adapters import BlockingConnection
from pika import BasicProperties

connection = BlockingConnection()
channel    = connection.channel()

queue_name = 'microservice.queue.1'
args       = {"x-queue-master-locator": "min-masters"}

channel.queue_declare(queue=queue_name, durable=True, arguments=args )

channel.basic_publish(exchange    = '',
                      routing_key = queue_name,
                      body        = 'Test message')
print(" [x] Sent 'test message!'")

connection.close()
