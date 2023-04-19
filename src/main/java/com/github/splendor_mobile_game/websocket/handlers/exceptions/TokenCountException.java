package com.github.splendor_mobile_game.websocket.handlers.exceptions;

/**
 * Exception thrown when number of tokens is inncorrect
 */
public class TokenCountException extends Exception{
    public TokenCountException() {
    }

    public TokenCountException(String message) {
        super(message);
    }

    public TokenCountException(Throwable cause) {
        super(cause);
    }

    public TokenCountException(String message, Throwable cause) {
        super(message, cause);
    } 
}
