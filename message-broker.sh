#!/bin/bash
mvn clean install -pl "message-broker" -am
java -jar ./message-broker/target/message-broker-1.0-SNAPSHOT-jar-with-dependencies.jar