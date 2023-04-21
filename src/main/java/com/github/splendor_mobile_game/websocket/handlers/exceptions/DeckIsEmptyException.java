package com.github.splendor_mobile_game.websocket.handlers.exceptions;

/**
 * Exception is thrown when there are no cards in the deck.
 */
public class DeckIsEmptyException extends Exception {
    public DeckIsEmptyException() {
    }

    public DeckIsEmptyException(String message) {
        super(message);
    }

    public DeckIsEmptyException(Throwable cause) {
        super(cause);
    }

    public DeckIsEmptyException(String message, Throwable cause) {
        super(message, cause);
    } 
    
}
