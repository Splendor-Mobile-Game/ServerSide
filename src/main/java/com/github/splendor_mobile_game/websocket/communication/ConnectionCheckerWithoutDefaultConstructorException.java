package com.github.splendor_mobile_game.websocket.communication;

/**
 * Exception thrown when attempting to create an instance of a connection handler class 
 * that does not have a default constructor. Right now, ConnectionChecker must have
 * default constructor.
 */
public class ConnectionCheckerWithoutDefaultConstructorException extends Exception {
    
    /** Constructs a new ConnectionCheckerWithoutDefaultConstructorException with no detail message. */
    public ConnectionCheckerWithoutDefaultConstructorException() {
    }

    /**
     * Constructs a new ConnectionCheckerWithoutDefaultConstructorException with the specified detail message.
     * @param message The detail message.
     */
    public ConnectionCheckerWithoutDefaultConstructorException(String message) {
        super(message);
    }

    /**
     * Constructs a new ConnectionCheckerWithoutDefaultConstructorException with the specified cause.
     * @param cause The cause of the exception.
     */
    public ConnectionCheckerWithoutDefaultConstructorException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ConnectionCheckerWithoutDefaultConstructorException with the specified detail message and cause.
     * @param message The detail message.
     * @param cause The cause of the exception.
     */
    public ConnectionCheckerWithoutDefaultConstructorException(String message, Throwable cause) {
        super(message, cause);
    }
}
