package com.chirikhin.player.socket;

import com.chirikhin.messagebroker.Message;
import com.chirikhin.messagebroker.MessageRequest;
import com.chirikhin.messagebroker.SubscriptionRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static final String FIRST_MESSAGE = "Message";
    private static final int MAX_MESSAGE_COUNTER = 10;

    private final int messageBrokerPort;
    private final String messageBrokerIpAddress;
    private final boolean isActive;
    private final String inputQueueName;
    private final String outputQueueName;

    private int messageCounter = 0;

    Client(int messageBrokerPort, String messageBrokerAddress, boolean active, String inputQueueName,
           String outputQueueName) {
        this.messageBrokerPort = messageBrokerPort;
        this.messageBrokerIpAddress = messageBrokerAddress;
        this.isActive = active;
        this.inputQueueName = inputQueueName;
        this.outputQueueName = outputQueueName;
    }

    void run() {
        try (Socket socket = new Socket(messageBrokerIpAddress, messageBrokerPort)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
                sendSetInputQueueMessage(objectOutputStream);

                if (isActive) {
                    Message message = new Message(String.format("%s %d", FIRST_MESSAGE, messageCounter));
                    sendTextMessage(objectOutputStream, new MessageRequest(message, outputQueueName));
                    LOGGER.info(String.format("The first message %s was sent", message.getText()));
                }

                while(messageCounter < MAX_MESSAGE_COUNTER) {
                    Object object = objectInputStream.readObject();

                    if (object instanceof Message) {
                        Message message = (Message) object;
                        String newTextMessage = String.format("%s %d", message.getText(), messageCounter);
                        sendTextMessage(objectOutputStream, new MessageRequest(new Message(newTextMessage),
                                outputQueueName));
                    } else {
                        LOGGER.severe(String.format("The deserialized message has an unexpected type: %s." +
                                        " The expected type is %s", object.getClass().getName(),
                                Message.class.getName()));
                        return;
                    }
                }

            } catch (IOException e) {
                LOGGER.severe(String.format("An error occurred during working with socket's streams. The details: %s",
                        e.toString()));
            } catch (ClassNotFoundException e) {
                LOGGER.severe(String.format("An error occurred during object deserialization. The details: %s",
                        e.getCause()));
            }
        } catch (IOException e) {
            LOGGER.severe(String.format("Failed to setup a connection to a message broker by the following address:" +
                    " %s:%s. The details: %s", messageBrokerIpAddress, messageBrokerPort, e.getMessage()));
        }
    }

    private void sendSetInputQueueMessage(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(new SubscriptionRequest(inputQueueName));
        LOGGER.info(String.format("The input setting message was sent. The queue: %s", inputQueueName));
    }

    private void sendTextMessage(ObjectOutputStream objectOutputStream, MessageRequest messageRequest)
            throws IOException {
        objectOutputStream.writeObject(messageRequest);
        messageCounter++;
        LOGGER.info(String.format("The message %s was sent to the queue %s. The message counter: %d",
                messageRequest.getMessage().getText(), messageRequest.getQueueName(), messageCounter));
    }
}
