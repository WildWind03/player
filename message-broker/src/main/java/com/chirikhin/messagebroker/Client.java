package com.chirikhin.messagebroker;

import com.chirikhin.player.Player;

import java.io.*;
import java.util.logging.Logger;

/**
 * It is a client representation on the message broker side. During running, it reads messages from the specified input
 * stream and then handles that messages in different ways based on their types
 */
public class Client implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final MessageBroker messageBroker;
    private final String id;
    private final OnClientFinishedHandler onFinishHandler;

    private Thread playerThread;

    Client(InputStream inputStream, OutputStream outputStream, MessageBroker messageBroker, String id,
           OnClientFinishedHandler onFinishHandler)
            throws IOException {
        this.objectOutputStream = new ObjectOutputStream(outputStream);
        this.objectInputStream = new ObjectInputStream(inputStream);
        this.messageBroker = messageBroker;
        this.id = id;
        this.onFinishHandler = onFinishHandler;
    }

    @Override
    public void run() {
        LOGGER.info("The client started running");

        try {
            while(!Thread.currentThread().isInterrupted()) {
                Object object;

                try {
                    object = objectInputStream.readObject();
                } catch (ClassNotFoundException e) {
                    LOGGER.severe(String.format("An error occurred during deserialization. The object will be " +
                            "ignored. The details: %s", e.getMessage()));
                    continue;
                }

                LOGGER.info("The new object was successfully read");

                if (!(object instanceof ServerRequest)) {
                    LOGGER.severe(String.format("A serialized object is not an instance of the ServerRequest class. "
                            + "It'll be ignored. It is an instance of %s", object.getClass().getName()));
                    continue;
                }

                LOGGER.info("New message processing has been started");

                ServerRequest serverRequest = (ServerRequest) object;
                serverRequest.process(this);
            }
        } catch (IOException e) {
            LOGGER.info(String.format("IO error was got. The client is going to end its execution. The details: %s",
                    e.getMessage()));
            playerThread.interrupt();

            try {
                playerThread.join();
            } catch (InterruptedException ex) {
                LOGGER.severe("Failed to join the player thread");
            }

            onFinishHandler.handler(this);
        }
    }

    String getId() {
        return id;
    }

    void handleSendMessageRequest(MessageRequest messageRequest) {
        LOGGER.info(String.format("Started handling a text message. The message: %s. The queue: %s",
                messageRequest.getMessage(), messageRequest.getQueueName()));

        Message message = messageRequest.getMessage();
        String queueName = messageRequest.getQueueName();

        boolean added = messageBroker.add(message, queueName);

        if (!added) {
            LOGGER.severe(String.format("Failed to add a message to the message broker. The message: %s. " +
                            "The queue: %s", message.getText(), queueName));
        }
    }

    void handleSetInputQueueRequest(SubscriptionRequest subscriptionRequest) {
        LOGGER.info(String.format("Started handling a subscription message. The queue: %s ",
                subscriptionRequest.getQueueName()));

        Player player = new Player(messageBroker, id, subscriptionRequest.getQueueName()) {
            @Override
            public void sendMessage(Message message) {
                Client.this.sendMessage(message);
            }
        };

        playerThread = new Thread(player);
        playerThread.start();
    }

    private void sendMessage(Message message) {
        try {
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            LOGGER.severe(String.format("Failed to write the message %s to a socket. The details: %s",
                    message.getText(), e.getMessage()));
        }
    }
}
