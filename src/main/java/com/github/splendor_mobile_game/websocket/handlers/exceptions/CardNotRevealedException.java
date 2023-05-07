package com.github.splendor_mobile_game.websocket.handlers.exceptions;

/**
 * Exception thrown when card is not in database or in any set
 */
public class CardNotRevealedException extends Exception{
    public CardNotRevealedException() {
    }

    public CardNotRevealedException(String message) {
        super(message);
    }

    public CardNotRevealedException(Throwable cause) {
        super(cause);
    }

    public CardNotRevealedException(String message, Throwable cause) {
        super(message, cause);
    }
}
