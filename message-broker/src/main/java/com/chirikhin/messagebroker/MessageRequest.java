package com.chirikhin.messagebroker;

/**
 * A server request that should be used to put a text message to the specified queue
 */
public class MessageRequest extends ServerRequest {
    private final Message message;
    private final String queueName;

    public MessageRequest(Message message, String queueName) {
        this.message = message;
        this.queueName = queueName;
    }

    public Message getMessage() {
        return message;
    }

    public String getQueueName() {
        return queueName;
    }

    @Override
    void process(Client client) {
        client.handleSendMessageRequest(this);
    }
}
