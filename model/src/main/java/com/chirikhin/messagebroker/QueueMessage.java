package com.chirikhin.messagebroker;

import java.io.Serializable;

public class QueueMessage implements Serializable {
    private final String queueName;
    private final Message message;

    QueueMessage(String queueName, Message message) {
        this.queueName = queueName;
        this.message = message;
    }

    String getQueueName() {
        return queueName;
    }

    public Message getMessage() {
        return message;
    }
}