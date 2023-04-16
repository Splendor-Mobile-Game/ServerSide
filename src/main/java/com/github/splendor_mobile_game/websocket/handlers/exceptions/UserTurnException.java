package com.github.splendor_mobile_game.websocket.handlers.exceptions;

/**
 * Exception thrown when attempting to do some action when it
 * is not user's turn
 */
public class UserTurnException extends Exception{
    public UserTurnException() {
    }

    public UserTurnException(String message) {
        super(message);
    }

    public UserTurnException(Throwable cause) {
        super(cause);
    }

    public UserTurnException(String message, Throwable cause) {
        super(message, cause);
    }
}
