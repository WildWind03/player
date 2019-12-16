package com.chirikhin.messagebroker;

public class Main {
    private static final int PORT = 9005;

    public static void main(String[] args) {
        Server server = new Server(PORT);
        server.run();
    }
}
