package com.chirikhin.player.thread;

import com.chirikhin.messagebroker.Message;
import com.chirikhin.messagebroker.MessageBroker;
import com.chirikhin.player.Player;

public class ThreadPlayer extends Player {
    private final MessageBroker messageBroker;
    private final String outputQueueName;

    ThreadPlayer(MessageBroker messageBroker, String id, String inputQueueName, String outputQueueName) {
        super(messageBroker, id, inputQueueName);
        this.messageBroker = messageBroker;
        this.outputQueueName = outputQueueName;
    }

    @Override
    public void sendMessage(Message message) {
        String newMessageText = String.format("%s %d", message.getText(), getSentMessagesCounter());
        Message newMessage = new Message(newMessageText);
        messageBroker.add(newMessage, outputQueueName);
    }
}
