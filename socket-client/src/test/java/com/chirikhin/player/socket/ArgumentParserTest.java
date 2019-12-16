package com.chirikhin.player.socket;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ArgumentParserTest {

    private final ArgumentParser argumentParser = new ArgumentParser();

    private static Stream<Arguments> provideArgsForParse() {
        return Stream.of(null,
                Arguments.of((Object) new String[] {"OUTPUT1", "OUTPUT2"}),
                Arguments.of((Object) new String[] {"OUTPUT1"}),
                Arguments.of((Object) new String[] {"OUTPUT1", "OUTPUT1"}),
                Arguments.of((Object) new String[] {null, null}),
                Arguments.of((Object) new String[] {"OUTPUT1", "OUTPUT2", "OUTPUT3"}),
                Arguments.of((Object) new String[] {"OUTPUT1", "OUTPUT2", "passive", "OUTPUT3"}));
    }

    @Test
    void parse_appPropertiesOfActiveClientAreParsed_ifArgsAreValid() {
        String[] args = new String[] {"OUTPUT1", "OUTPUT2", "active"};
        AppProperties appProperties = argumentParser.parse(args);
        Assertions.assertEquals("OUTPUT2", appProperties.getInputQueueName());
        Assertions.assertEquals("OUTPUT1", appProperties.getOutputQueueName());
        Assertions.assertTrue(appProperties.isActive());
    }

    @Test
    void parse_appPropertiesOfPassiveClientAreParsed_ifArgsAreValid() {
        String[] args = new String[] {"OUTPUT1", "OUTPUT2", "passive"};
        AppProperties appProperties = argumentParser.parse(args);
        Assertions.assertEquals("OUTPUT2", appProperties.getInputQueueName());
        Assertions.assertEquals("OUTPUT1", appProperties.getOutputQueueName());
        Assertions.assertFalse(appProperties.isActive());
    }

    @MethodSource("provideArgsForParse")
    @ParameterizedTest
    void parse_returnsNull_ifArgsAreNotValid(String[] args) {
        AppProperties appProperties = argumentParser.parse(args);
        Assertions.assertNull(appProperties);
    }
}