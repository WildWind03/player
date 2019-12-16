package com.chirikhin.messagebroker;

import java.io.Serializable;

/**
 * A base class for server requests. All others request should extends this class
 */
abstract class ServerRequest implements Serializable {
    abstract void process(Client client);
}
