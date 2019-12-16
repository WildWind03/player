package com.chirikhin.messagebroker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private final int port;

    private final MessageBroker messageBroker = new MessageBroker();
    private final Thread messageBrokerThread = new Thread(messageBroker);
    private final Map<String, Client> clientByIdMap = new ConcurrentHashMap<>();
    private final Map<String, Thread> clientThreadByClientIdMap = new ConcurrentHashMap<>();

    private volatile boolean isRunning = true;

    private ServerSocket serverSocket;
    private boolean isStarted;
    private int clientsCounter = 0;

    Server(int port) {
        this.port = port;
    }

    /**
     * Starts the server. It will be waiting for clients to connect and then will stop running when all connected
     * clients will finish their jobs. The method cannot be used by multiple threads and more than once
     * @throws IllegalStateException if the method has been previously called
     */
    void run() {
        if (isStarted) {
            throw new IllegalStateException("The method 'run' has already been called. It is illegal to call " +
                    "it again");
        } else {
            isStarted = true;
        }

        messageBrokerThread.start();

        try {
            serverSocket = new ServerSocket(port);

            while (isRunning) {
                Socket newSocket = serverSocket.accept();

                Client client = new Client(newSocket.getInputStream(), newSocket.getOutputStream(), messageBroker,
                        String.valueOf(clientsCounter), this::onClientFinished);

                LOGGER.info(String.format("The client %d is created", clientsCounter));

                clientByIdMap.put(String.valueOf(clientsCounter), client);

                Thread clientThread = new Thread(client);
                clientThreadByClientIdMap.put(String.valueOf(clientsCounter), clientThread);
                clientThread.start();

                clientsCounter++;
            }
        } catch (IOException e) {
            LOGGER.severe(String.format("An error occurred during performing some action on the server socket. " +
                    "The details: %s", e.toString()));
            if (null != serverSocket && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    LOGGER.info(String.format("Failed to close the server socket. The details: %s", ex.getMessage()));
                }
            }
        }

        messageBrokerThread.interrupt();
        clientThreadByClientIdMap.values().forEach(Thread::interrupt);

        for (Map.Entry<String, Thread> entry : clientThreadByClientIdMap.entrySet()) {
            Thread thread = entry.getValue();
            String clientId = entry.getKey();

            try {
                thread.join();
            } catch (InterruptedException e) {
                LOGGER.severe(String.format("Failed to join the thread of the client %s", clientId));
            }
        }

        try {
            messageBrokerThread.join();
        } catch (InterruptedException e) {
            LOGGER.severe("Failed to join the thread of the message broker thread");
        }
    }

    private void onClientFinished(Client client) {
        clientByIdMap.remove(client.getId());
        clientThreadByClientIdMap.remove(client.getId());

        if (clientByIdMap.isEmpty()) {
            isRunning = false;

            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.severe(String.format("Failed to close the server socket. The details: %s", e.getMessage()));
            }
        }
    }
}
