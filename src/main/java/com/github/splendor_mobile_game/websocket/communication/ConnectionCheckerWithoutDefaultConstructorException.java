package com.github.splendor_mobile_game.websocket.communication;

/**
 * Exception thrown when attempting to create an instance of a connection handler class 
 * that does not have a default constructor. Right now, ConnectionChecker must have
 * default constructor.
 */
public class ConnectionCheckerWithoutDefaultConstructorException extends Exception {
    
    public ConnectionCheckerWithoutDefaultConstructorException() {
    }

    public ConnectionCheckerWithoutDefaultConstructorException(String message) {
        super(message);
    }

    public ConnectionCheckerWithoutDefaultConstructorException(Throwable cause) {
        super(cause);
    }

    public ConnectionCheckerWithoutDefaultConstructorException(String message, Throwable cause) {
        super(message, cause);
    }
}
