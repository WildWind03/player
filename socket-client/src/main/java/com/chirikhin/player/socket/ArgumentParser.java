package com.chirikhin.player.socket;

import java.util.Objects;
import java.util.logging.Logger;

class ArgumentParser {
    private static final Logger LOGGER = Logger.getLogger(ArgumentParser.class.getName());

    private static final int EXPECTED_AMOUNT_OF_ARGS = 3;
    private static final String ACTIVE_PLAYER_ARG = "active";
    private static final String PASSIVE_PLAYER_ARG = "passive";

    /**
     * Parses args into an {@link AppProperties} instance
     * @param args an array of the arguments to be parsed which should contain arguments in the following order:
     *             an output queue name, an input queue name, a client type (can be either 'active' or 'passive').
     *             Note that the output queue name must be different from the input one
     * @return an instance of {@link AppProperties}
     */
    AppProperties parse(String[] args) {
        if (null == args) {
            LOGGER.severe("Args cannot be null");
            return null;
        }

        if (EXPECTED_AMOUNT_OF_ARGS != args.length) {
            LOGGER.severe(String.format("Failed to start the application. Actual amount of argument is %s while " +
                    "the expected amount is %s", args.length, EXPECTED_AMOUNT_OF_ARGS));
            return null;
        }

        String outputQueueName = args[0];

        if (null == outputQueueName || outputQueueName.isEmpty()) {
            LOGGER.severe("Output queue name cannot be null");
            return null;
        }

        String inputQueueName = args[1];

        if (null == inputQueueName || inputQueueName.isEmpty()) {
            LOGGER.severe("Input queue name cannot be null");
            return null;
        }

        if (Objects.equals(inputQueueName, outputQueueName)) {
            LOGGER.severe("Input queue ane output queue name must be different");
            return null;
        }

        boolean isActive = false;

        if (Objects.equals(ACTIVE_PLAYER_ARG, args[2])) {
            isActive = true;
        } else if (!Objects.equals(PASSIVE_PLAYER_ARG, args[2])) {
            LOGGER.severe(String.format("The isActive flag must be either %s or %s. The actual value is %s",
                    ACTIVE_PLAYER_ARG, PASSIVE_PLAYER_ARG, args[2]));
            return null;
        }

        return new AppProperties(outputQueueName, inputQueueName, isActive);
    }
}
