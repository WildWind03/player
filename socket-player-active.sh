#!/bin/bash
mvn clean install -pl "socket-client" -am
java -jar ./socket-client/target/socket-client-1.0-SNAPSHOT-jar-with-dependencies.jar OUTPUT1 OUTPUT2 active