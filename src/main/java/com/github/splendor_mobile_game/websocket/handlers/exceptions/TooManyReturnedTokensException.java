package com.github.splendor_mobile_game.websocket.handlers.exceptions;

/** Too many returned tokens exception, used when user has to return some tokens but gives back too much */
public class TooManyReturnedTokensException extends Exception {

    public TooManyReturnedTokensException() {
    }

    public TooManyReturnedTokensException(String message) {
        super(message);
    }

    public TooManyReturnedTokensException(Throwable cause) {
        super(cause);
    }

    public TooManyReturnedTokensException(String message, Throwable cause) {
        super(message, cause);
    }

}