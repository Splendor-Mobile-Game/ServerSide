package com.github.splendor_mobile_game.websocket.communication;

public class ConnectionHandlerWithoutDefaultConstructorException extends Exception {
    public ConnectionHandlerWithoutDefaultConstructorException() {
    }

    public ConnectionHandlerWithoutDefaultConstructorException(String message) {
        super(message);
    }

    public ConnectionHandlerWithoutDefaultConstructorException(Throwable cause) {
        super(cause);
    }

    public ConnectionHandlerWithoutDefaultConstructorException(String message, Throwable cause) {
        super(message, cause);
    }
}
