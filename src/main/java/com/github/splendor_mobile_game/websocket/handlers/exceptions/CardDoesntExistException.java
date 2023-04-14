package com.github.splendor_mobile_game.websocket.handlers.exceptions;

/**
 * Exception thrown when card is not in database or in any set
 */
public class CardDoesntExistException extends Exception{
    public CardDoesntExistException() {
    }

    public CardDoesntExistException(String message) {
        super(message);
    }

    public CardDoesntExistException(Throwable cause) {
        super(cause);
    }

    public CardDoesntExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
