package com.chirikhin.player.socket;

public class Main {
    private static final int MESSAGE_BROKER_PORT = 9005;
    private static final String MESSAGE_BROKER_ADDRESS = "127.0.0.1";

    public static void main(String[] args) {
        ArgumentParser argumentParser = new ArgumentParser();
        AppProperties appProperties = argumentParser.parse(args);

        if (null == appProperties) {
            return;
        }

        Client client = new Client(MESSAGE_BROKER_PORT, MESSAGE_BROKER_ADDRESS, appProperties.isActive(),
                appProperties.getInputQueueName(), appProperties.getOutputQueueName());
        client.run();
    }
}
