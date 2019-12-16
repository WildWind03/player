package com.chirikhin.messagebroker;

/**
 * A server request that should be used to set a queue the client wants to get messages from
 */
public class SubscriptionRequest extends ServerRequest {
    private final String queueName;

    public SubscriptionRequest(String queueName) {
        this.queueName = queueName;
    }

    String getQueueName() {
        return queueName;
    }

    void process(Client client) {
        client.handleSetInputQueueRequest(this);
    }
}
