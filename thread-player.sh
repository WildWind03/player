#!/bin/bash
mvn clean install -pl "thread-player" -am
java -jar ./thread-player/target/thread-player-1.0-SNAPSHOT-jar-with-dependencies.jar