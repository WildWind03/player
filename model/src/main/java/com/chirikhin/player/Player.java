package com.chirikhin.player;

import com.chirikhin.messagebroker.Message;
import com.chirikhin.messagebroker.MessageBroker;

import java.util.logging.Logger;

/**
 * A player reads messages from an input queue and send them according to its
 * {@link Player#sendMessage} method implementation
 */
public abstract class Player implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Player.class.getName());
    private static final int MAX_MESSAGE_AMOUNT = 10;

    private final MessageBroker messageBroker;
    private final String id;
    private final String inputQueueName;

    private int sentMessagesCounter = 0;

    public Player(MessageBroker messageBroker, String id, String inputQueueName) {
        this.messageBroker = messageBroker;
        this.id = id;
        this.inputQueueName = inputQueueName;
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted() && sentMessagesCounter < MAX_MESSAGE_AMOUNT) {
                Message message = messageBroker.take(inputQueueName);
                handleMessage(message);
            }
        } catch (InterruptedException e) {
            LOGGER.info(String.format("The player %s was interrupted", id));
        }

        LOGGER.info(String.format("The player %s has finished", id));
    }

    public void handleMessage(Message messageFromQueue) {
        sendMessage(messageFromQueue);
        ++sentMessagesCounter;
        LOGGER.info(String.format("The player %s successfully handled the message: %s", id,
                messageFromQueue));
    }


    protected int getSentMessagesCounter() {
        return sentMessagesCounter;
    }

    abstract protected void sendMessage(Message message);
}
