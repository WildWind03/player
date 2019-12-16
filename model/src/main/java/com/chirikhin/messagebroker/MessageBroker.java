package com.chirikhin.messagebroker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * An extremely simplified thread-safe implementation of a message broker pattern
 */
public class MessageBroker implements Runnable {
    private static final int QUEUE_SIZE = 200;
    private static final Logger LOGGER = Logger.getLogger(MessageBroker.class.getName());

    private final BlockingQueue<QueueMessage> messageQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);

    private final ConcurrentMap<String, BlockingQueue<Message>> processedMessagesByQueueId = new ConcurrentHashMap<>();

    /**
     * Adds the message to the internal message queue. The message will be processed later in the message broker thread.
     *
     * @param message a message to put
     * @return true if the message was actually put or false otherwise
     */
    public boolean add(Message message, String queue) {
        boolean added = messageQueue.add(new QueueMessage(queue, message));

        if (added) {
            LOGGER.info(String.format("The message %s was successfully added to the internal queue and will " +
                            "processed later", message.getText()));
        }

        return added;
    }

    public void run() {
        LOGGER.info("The message broker has started");

        try {
            while (!Thread.currentThread().isInterrupted()) {
                QueueMessage queueMessage = messageQueue.take();

                String queueName = queueMessage.getQueueName();
                Message message = queueMessage.getMessage();

                LOGGER.info(String.format("The message %s has been taken", message.getText()));

                processedMessagesByQueueId.putIfAbsent(queueName, new LinkedBlockingQueue<>(QUEUE_SIZE));

                BlockingQueue<Message> messages = processedMessagesByQueueId.get(queueName);

                boolean messagedAdded = messages.add(queueMessage.getMessage());

                if (!messagedAdded) {
                    LOGGER.info(String.format("Failed to add a message to the queue %s. The message: %s", queueName,
                            message.getText()));
                } else {
                    LOGGER.info(String.format("The message %s has been added to the queue %s", message.getText(),
                            queueName));
                }
            }
        } catch (InterruptedException e) {
            LOGGER.info("The message broker was interrupted");
        }

        LOGGER.info("The message broker has finished");
    }

    /**
     * Takes and removes the first message from the specified queue. If the queue is full, the calling thread waits
     *
     * @param queueName a queue to take a message from
     * @return the first message in the specified queue
     * @throws InterruptedException if the thread was interrupted during waiting
     */
    public Message take(String queueName) throws InterruptedException {
        processedMessagesByQueueId.putIfAbsent(queueName, new LinkedBlockingQueue<>(QUEUE_SIZE));
        BlockingQueue<Message> messages = processedMessagesByQueueId.get(queueName);
        return messages.take();
    }
}
