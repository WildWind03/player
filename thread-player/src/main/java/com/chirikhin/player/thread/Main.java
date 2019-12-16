package com.chirikhin.player.thread;

import com.chirikhin.messagebroker.Message;
import com.chirikhin.messagebroker.MessageBroker;
import com.chirikhin.player.Player;

import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String SECOND_PLAYER_OUTPUT_QUEUE = "INITIATOR_PLAYER_MESSAGES_QUEUE";
    private static final String FIRST_PLAYER_OUTPUT_QUEUE = "PASSIVE_PLAYER_MESSAGES_QUEUE";
    private static final String FIRST_MESSAGE = "Message";

    private static final String FIRST_PLAYER_ID = "1";
    private static final String SECOND_PLAYER_ID = "2";

    public static void main(String[] args) {
        MessageBroker messageBroker = new MessageBroker();

        Player firstPlayer = new ThreadPlayer(messageBroker, FIRST_PLAYER_ID, SECOND_PLAYER_OUTPUT_QUEUE,
                FIRST_PLAYER_OUTPUT_QUEUE);
        firstPlayer.handleMessage(new Message(FIRST_MESSAGE));

        Player secondPlayer = new ThreadPlayer(messageBroker, SECOND_PLAYER_ID, FIRST_PLAYER_OUTPUT_QUEUE,
                SECOND_PLAYER_OUTPUT_QUEUE);

        Thread firstPlayerThread = new Thread(firstPlayer);
        Thread secondPlayerThread = new Thread(secondPlayer);
        Thread messageBrokerThread  = new Thread(messageBroker);

        firstPlayerThread.start();
        secondPlayerThread.start();
        messageBrokerThread.start();

        try {
            firstPlayerThread.join();
            secondPlayerThread.join();

            messageBrokerThread.interrupt();
            messageBrokerThread.join();
        } catch (InterruptedException e) {
            LOGGER.info("The main thread was interrupted during players and message broker joining");
        }
    }
}
