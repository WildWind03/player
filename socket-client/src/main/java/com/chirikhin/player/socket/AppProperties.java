package com.chirikhin.player.socket;

class AppProperties {
    private final String outputQueueName;
    private final String inputQueueName;
    private final boolean active;

    AppProperties(String outputQueueName, String inputQueueName, boolean active) {
        this.outputQueueName = outputQueueName;
        this.inputQueueName = inputQueueName;
        this.active = active;
    }

    String getOutputQueueName() {
        return outputQueueName;
    }

    String getInputQueueName() {
        return inputQueueName;
    }

    boolean isActive() {
        return active;
    }
}
