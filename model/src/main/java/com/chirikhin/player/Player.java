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

    /**
     * Handles the message by incrementing the message counter and calling {@link Player#sendMessage(Message)}
     * @param message a message to handle
     */
    public void handleMessage(Message message) {
        sendMessage(message);
        ++sentMessagesCounter;
        LOGGER.info(String.format("The player %s successfully handled the message: %s", id,
                message.getText()));
    }

    protected int getSentMessagesCounter() {
        return sentMessagesCounter;
    }

    /**
     * The method is executed after {@link Player#handleMessage(Message)} and is designed to expressly implement
     * the logic of sending message
     * @param message a message to send
     */
    abstract protected void sendMessage(Message message);
}
